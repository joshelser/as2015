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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

import cereal.Field;
import cereal.InstanceOrBuilder;
import cereal.Mapping;
import cereal.impl.FieldImpl;

/**
 * 
 */
public class ReviewMapping implements Mapping<CsvReview> {

  private static final ColumnVisibility EMPTY = new ColumnVisibility("");

  @Override
  public Text getRowId(CsvReview obj) {
    return new Text(Integer.toHexString(obj.hashCode()));
  }

  @Override
  public List<Field> getFields(CsvReview obj) {
    List<Field> fields = new LinkedList<>();
    for (int i = 0; i < obj.getHeader().size() && i < obj.getValues().size(); i++) {
      String header = obj.getHeader().get(i);
      String value = obj.getValues().get(i);

      int index = header.indexOf(':');
      if (-1 == index) {
        throw new IllegalArgumentException("Could not split header '" + header + "'");
      }

      fields.add(new FieldImpl(new Text(header.substring(index + 1)), new Text(header.substring(0, index)), EMPTY, new Value(value.getBytes(UTF_8))));
    }

    return fields;
  }

  @Override
  public void update(Iterable<Entry<Key,Value>> entry, InstanceOrBuilder<CsvReview> obj) {
    // Not yet implemented
    throw new UnsupportedOperationException();
  }

  @Override
  public Class<CsvReview> objectType() {
    return CsvReview.class;
  }
  
}
