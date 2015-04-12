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
package joshelser.as2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

/**
 * Parses reviews scraped from Amazon obtained from https://snap.stanford.edu/data/web-Amazon.html
 */
public class AmazonReviewParser implements Iterator<AmazonReview> {
  
  private final BufferedReader reader;
  private boolean readerExhausted = false;
  private AmazonReview currentReview = null;
  
  public AmazonReviewParser(BufferedReader reader) {
    this.reader = reader;
    currentReview = getNextReview();
  }
  
  @Override
  public AmazonReview next() {
    AmazonReview prevReview = currentReview;
    currentReview = getNextReview();
    return prevReview;
  }
  
  @Override
  public boolean hasNext() {
    return null != currentReview;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove is not supported");
  }
  
  /**
   * Compute the next review from the reader.
   * 
   * @return The next review or null if there is none
   */
  AmazonReview getNextReview() {
    if (readerExhausted) {
      return null;
    }
    
    try {
      String line;
      AmazonReview nextReview = null;
      do {
        line = reader.readLine();
        if (null == line) {
          readerExhausted = true;
          return nextReview;
        }
        
        // Empty line is message separator
        if (StringUtils.isBlank(line)) {
          // If we have a review return it
          if (null != nextReview) {
            return nextReview;
          }
          // otherwise, just try to read the next message
        } else {
          if (null == nextReview) {
            nextReview = new AmazonReview();
          }
          int index = line.indexOf(':');
          if (-1 == index) {
            throw new IllegalArgumentException("Cannot parse line '" + line + "'");
          }
          String key = StringUtils.strip(line.substring(0, index));
          String value = StringUtils.strip(line.substring(index + 1));
          
          index = key.indexOf('/');
          if (-1 == index) {
            throw new IllegalArgumentException("Cannot parse key '" + key + "'");
          }
          AmazonReviewField field = new AmazonReviewField(key.substring(0, index), key.substring(index + 1));
          nextReview.addReviewField(field, ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)));
        }
      } while (!readerExhausted);
      return nextReview;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file", e);
    }
  }
}
