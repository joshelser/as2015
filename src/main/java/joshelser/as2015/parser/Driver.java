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
package joshelser.as2015.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

import au.com.bytecode.opencsv.CSVWriter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * 
 */
public class Driver {

  private static class Opts {
    @Parameter(names = {"-c", "--category"}, description = "Category of the review data", required = true)
    private String category;

    @Parameter(names = {"-f", "--file"}, description = "File to read", required = true)
    private File file;

    @Parameter(names = {"-of", "--output-file"}, description = "File to write to", required = true)
    private File outputFile;
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

    try (BufferedReader reader = new BufferedReader(new FileReader(options.file));
        CSVWriter writer = new CSVWriter(new FileWriter(options.outputFile), (char) 1)) {
      AmazonReviewParser parser = new AmazonReviewParser(reader);

      long recordsParsed = 0l;
      while (parser.hasNext()) {
        AmazonReview review = parser.next();
        review.addReviewField(new AmazonReviewField("category", ""), ByteBuffer.wrap(options.category.getBytes(StandardCharsets.UTF_8)));

        if (0 == recordsParsed) {
          Set<AmazonReviewField> keys = review.getAttributes().keySet();
          String[] header = new String[keys.size()];
          Iterator<AmazonReviewField> iter = keys.iterator();
          for (int i = 0; i < header.length; i++) {
            AmazonReviewField field = iter.next();
            header[i] = field.getCategory() + ":" + field.getName();
          }
          writer.writeNext(header);
        }

        recordsParsed++;

        writer.writeNext(review.toArray());
      }
    }
  }
}
