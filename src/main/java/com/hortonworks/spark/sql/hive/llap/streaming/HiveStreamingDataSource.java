/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hortonworks.spark.sql.hive.llap.streaming;

import java.util.Arrays;
import java.util.List;

import com.hortonworks.spark.sql.hive.llap.HiveWarehouseSession;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.StreamWriteSupport;
import org.apache.spark.sql.sources.v2.writer.streaming.StreamWriter;
import org.apache.spark.sql.sources.v2.SessionConfigSupport;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveStreamingDataSource implements DataSourceV2, StreamWriteSupport, SessionConfigSupport {
  private static Logger LOG = LoggerFactory.getLogger(HiveStreamingDataSource.class);

  @Override
  public StreamWriter createStreamWriter(final String queryId, final StructType schema, final OutputMode mode,
    final DataSourceOptions options) {
    return createDataSourceWriter(queryId, schema, options);
  }

  private HiveStreamingDataSourceWriter createDataSourceWriter(final String id, final StructType schema,
    final DataSourceOptions options) {
    String dbName = null;
    if(options.get("default.db").isPresent()) {
      dbName = options.get("default.db").get();
    } else {
      dbName = options.get("database").orElse("default");
    }
    String tableName = options.get("table").orElse(null);
    String partition = options.get("partition").orElse(null);
    List<String> partitionValues = partition == null ? null : Arrays.asList(partition.split(","));
    String metastoreUri = options.get("metastoreUri").orElse("thrift://localhost:9083");
    LOG.info("OPTIONS - database: {} table: {} partition: {} metastoreUri: {}", dbName,
      tableName, partition, metastoreUri);
    return new HiveStreamingDataSourceWriter(id, schema, dbName, tableName,
      partitionValues, metastoreUri);
  }

  @Override public String keyPrefix() {
    return HiveWarehouseSession.HIVE_WAREHOUSE_POSTFIX;
  }

}

