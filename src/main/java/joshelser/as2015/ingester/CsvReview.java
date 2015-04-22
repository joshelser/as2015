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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class CsvReview {

  private List<String> header;
  private List<String> values;

  public CsvReview(List<String> header, List<String> values) {
    checkNotNull(header);
    checkNotNull(values);
    this.header = header;
    this.values = values;
  }

  public List<String> getHeader() {
    return Collections.unmodifiableList(header);
  }

  public List<String> getValues() {
    return Collections.unmodifiableList(values);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((header == null) ? 0 : header.hashCode());
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CsvReview)) {
      return false;
    }
    CsvReview other = (CsvReview) obj;
    if (!header.equals(other.header)) {
      return false;
    }
    if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }
}
