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

import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A single review. Contains multiple attributes: pairs of {@link AmazonReviewField} and some value.
 */
public class AmazonReview {

  private final TreeMap<AmazonReviewField,ByteBuffer> reviewData;

  public AmazonReview() {
    reviewData = new TreeMap<>();
  }

  public void addReviewField(AmazonReviewField field, ByteBuffer data) {
    checkNotNull(field);
    checkNotNull(data);
    reviewData.put(field, data);
  }

  public Map<AmazonReviewField,ByteBuffer> getAttributes() {
    return Collections.unmodifiableMap(reviewData);
  }

  public String[] toArray() {
    String[] values = new String[reviewData.values().size()];
    Iterator<ByteBuffer> iter = reviewData.values().iterator();
    for (int i = 0; i < values.length; i++) {
      ByteBuffer value = iter.next();
      values[i] = new String(value.array(), value.arrayOffset(), value.limit());
    }
    return values;
  }
}
