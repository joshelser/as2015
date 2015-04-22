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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Identifies a field in an Amazon review 
 */
public class AmazonReviewField implements Comparable<AmazonReviewField> {

  private final String category;
  private final String name;

  public AmazonReviewField(String category, String name) {
    checkNotNull(category);
    checkNotNull(name);
    this.category = category;
    this.name = name;
  }

  /**
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "AmazonReviewField [category=" + category + ", name=" + name + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AmazonReviewField)) {
      return false;
    }

    AmazonReviewField other = (AmazonReviewField) obj;
    if (!category.equals(other.category)) {
      return false;
    }
    if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(AmazonReviewField other) {
    int res = category.compareTo(other.category);
    if (0 != res) {
      return res;
    }
    return name.compareTo(other.name);
  }
}
