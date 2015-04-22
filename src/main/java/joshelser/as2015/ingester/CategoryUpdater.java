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

import java.io.File;
import java.util.Collections;
import java.util.Map.Entry;

import javax.management.Query;

import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.ClientConfiguration;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * 
 */
public class CategoryUpdater {
  private static final Logger log = LoggerFactory.getLogger(Query.class);

  private static class Opts {
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

    commander.setProgramName("Query");
    try {
      commander.parse(args);
    } catch (ParameterException ex) {
      commander.usage();
      System.err.println(ex.getMessage());
      System.exit(1);
    }

    ClientConfiguration conf = ClientConfiguration.loadDefault();
    if (null != options.clientConfFile) {
      conf = new ClientConfiguration(new PropertiesConfiguration(options.clientConfFile));
    }
    conf.withInstance(options.instanceName).withZkHosts(options.zookeepers);

    ZooKeeperInstance inst = new ZooKeeperInstance(conf);
    Connector conn = inst.getConnector(options.user, new PasswordToken(options.password));

    BatchScanner bs = conn.createBatchScanner(options.table, Authorizations.EMPTY, 16);
    BatchWriter bw = conn.createBatchWriter(options.table, new BatchWriterConfig());
    try {
      bs.setRanges(Collections.singleton(new Range()));
      final Text categoryText = new Text("category");
      bs.fetchColumnFamily(categoryText);
  
      final Text categoryName = new Text("name");
      final Text row = new Text();
      for (Entry<Key,Value> entry : bs)  {
        entry.getKey().getRow(row);
        Mutation m = new Mutation(row);
        m.put(categoryText, categoryName, entry.getValue());

        bw.addMutation(m);
      }
    } finally {
      bs.close();
      bw.close();
    }
  }
  
}
