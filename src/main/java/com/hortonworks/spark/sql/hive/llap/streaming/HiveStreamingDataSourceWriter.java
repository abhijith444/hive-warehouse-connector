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

import java.util.List;

import org.apache.spark.sql.catalyst.InternalRow;
import org.apache.spark.sql.sources.v2.writer.DataWriterFactory;
import org.apache.spark.sql.sources.v2.writer.SupportsWriteInternalRow;
import org.apache.spark.sql.sources.v2.writer.WriterCommitMessage;
import org.apache.spark.sql.sources.v2.writer.streaming.StreamWriter;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hortonworks.spark.sql.hive.llap.HiveStreamingDataWriterFactory;

public class HiveStreamingDataSourceWriter implements SupportsWriteInternalRow, StreamWriter {
  private static Logger LOG = LoggerFactory.getLogger(HiveStreamingDataSourceWriter.class);

  private String jobId;
  private StructType schema;
  private String db;
  private String table;
  private List<String> partition;
  private String metastoreUri;

  public HiveStreamingDataSourceWriter(String jobId, StructType schema, String db,
    String table, List<String> partition, final String metastoreUri) {
    this.jobId = jobId;
    this.schema = schema;
    this.db = db;
    this.table = table;
    this.partition = partition;
    this.metastoreUri = metastoreUri;
  }

  @Override
  public DataWriterFactory<InternalRow> createInternalRowWriterFactory() {
    // for the streaming case, commit transaction happens on task commit() (atleast-once), so interval is set to -1
    return new HiveStreamingDataWriterFactory(jobId, schema, -1, db, table, partition, metastoreUri);
  }

  @Override
  public void commit(final long epochId, final WriterCommitMessage[] messages) {
    LOG.info("Commit job {}", jobId);
  }

  @Override
  public void abort(final long epochId, final WriterCommitMessage[] messages) {
    LOG.info("Abort job {}", jobId);
  }
}

