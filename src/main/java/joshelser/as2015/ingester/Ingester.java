/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package joshelser.as2015.ingester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.accumulo.core.client.ClientConfiguration;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cereal.Registry;
import cereal.Store;
import cereal.impl.RegistryImpl;
import cereal.impl.StoreImpl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * 
 */
public class Ingester {
  private static final Logger log = LoggerFactory.getLogger(Ingester.class);

  private static class Opts {
    @Parameter(names = {"-f", "--file"}, description = "File to read", required = true)
    private File file;

    @Parameter(names = {"-i", "--instance"}, description = "Accumulo instance name", required = true)
    private String instanceName;

    @Parameter(names = {"-z"}, description = "Zookeepers", required = true)
    private String zookeepers;

    @Parameter(names = {"-u"}, description = "Accumulo user", required = true)
    private String user;

    @Parameter(names = {"-p"}, description = "Accumulo password", required = false)
    private String password;

    @Parameter(names = {"--clientConf"}, description = "Accumulo client configuration file", required = false)
    private File clientConfFile;

    @Parameter(names = {"-t", "--table"}, description = "Accumulo table to write to", required = true)
    private String table;
  }

  public static void main(String[] args) throws Exception {
    JCommander commander = new JCommander();
    final Opts options = new Opts();
    commander.addObject(options);

    commander.setProgramName("Amazon Review Parser");
    try {
      commander.parse(args);
    } catch (ParameterException ex) {
      commander.usage();
      System.err.println(ex.getMessage());
      System.exit(1);
    }

    Registry registry = new RegistryImpl();
    registry.add(new ReviewMapping());

    ClientConfiguration conf = ClientConfiguration.loadDefault();
    if (null != options.clientConfFile) {
      conf = new ClientConfiguration(new PropertiesConfiguration(options.clientConfFile));
    }
    conf.withInstance(options.instanceName).withZkHosts(options.zookeepers);

    ZooKeeperInstance inst = new ZooKeeperInstance(conf);
    Connector conn = inst.getConnector(options.user, new PasswordToken(options.password));
    if (!conn.tableOperations().exists(options.table)) {
      conn.tableOperations().create(options.table);
    }

    log.info("Writing data from {}", options.file);

    long count = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(options.file));
        Store store = new StoreImpl(registry, conn, options.table)){
      List<String> schema = parseArray(readLine(reader));

      String[] record;
      while ((record = readLine(reader)) != null) {
        try {
          List<String> values = parseArray(record);
          CsvReview review = new CsvReview(schema, values);
          store.write(Collections.singleton(review));
          count++;
        } catch (Exception e) {
          log.error("Failed to parse '" + Arrays.toString(record) + "'", e);
        }
      }
    }

    log.info("Wrote {} records for {}", count, options.file);
  }

  private static String[] readLine(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (null == line) {
      return null;
    }
    return StringUtils.split(line, (char) 1);
  }

  private static List<String> parseArray(String[] values) {
    List<String> list = new ArrayList<>(values.length);
    for (String value : values) {
      // Strip off surrounding quotation marks
      list.add(value.substring(1, value.length() - 1));
    }
    return list;
  }
}
