/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iceberg;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Superinterface of {@link DataFile} and {@link DeleteFile} that exposes common methods.
 *
 * @param <F> the concrete Java class of a ContentFile instance.
 */
public interface ContentFile<F> {
  /**
   * Returns the ordinal position of the file in a manifest, or null if it was not read from a manifest.
   */
  Long pos();

  /**
   * Returns id of the partition spec used for partition metadata.
   */
  int specId();

  /**
   * Returns type of content stored in the file; one of DATA, POSITION_DELETES, or EQUALITY_DELETES.
   */
  FileContent content();

  /**
   * Returns fully qualified path to the file, suitable for constructing a Hadoop Path.
   */
  CharSequence path();

  /**
   * Returns format of the file.
   */
  FileFormat format();

  /**
   * Returns partition for this file as a {@link StructLike}.
   */
  StructLike partition();

  /**
   * Returns the number of top-level records in the file.
   */
  long recordCount();

  /**
   * Returns the file size in bytes.
   */
  long fileSizeInBytes();

  /**
   * Returns if collected, map from column ID to the size of the column in bytes, null otherwise.
   */
  Map<Integer, Long> columnSizes();

  /**
   * Returns if collected, map from column ID to the count of its non-null values, null otherwise.
   */
  Map<Integer, Long> valueCounts();

  /**
   * Returns if collected, map from column ID to its null value count, null otherwise.
   */
  Map<Integer, Long> nullValueCounts();

  /**
   * Returns if collected, map from column ID to its NaN value count, null otherwise.
   */
  Map<Integer, Long> nanValueCounts();

  /**
   * Returns if collected, map from column ID to value lower bounds, null otherwise.
   */
  Map<Integer, ByteBuffer> lowerBounds();

  /**
   * Returns if collected, map from column ID to value upper bounds, null otherwise.
   */
  Map<Integer, ByteBuffer> upperBounds();

  /**
   * Returns metadata about how this file is encrypted, or null if the file is stored in plain text.
   */
  ByteBuffer keyMetadata();

  /**
   * Returns list of recommended split locations, if applicable, null otherwise.
   * <p>
   * When available, this information is used for planning scan tasks whose boundaries
   * are determined by these offsets. The returned list must be sorted in ascending order.
   */
  List<Long> splitOffsets();

  /**
   * Returns the set of field IDs used for equality comparison, in equality delete files.
   * <p>
   * An equality delete file may contain additional data fields that are not used by equality
   * comparison. The subset of columns in a delete file to be used in equality comparison are
   * tracked by ID. Extra columns can be used to reconstruct changes and metrics from extra
   * columns are used during job planning.
   *
   * @return IDs of the fields used in equality comparison with the records in this delete file
   */
  List<Integer> equalityFieldIds();


  /**
   * Copies this file. Manifest readers can reuse file instances; use
   * this method to copy data when collecting files from tasks.
   *
   * @return a copy of this data file
   */
  F copy();

  /**
   * Copies this file without file stats. Manifest readers can reuse file instances; use
   * this method to copy data without stats when collecting files.
   *
   * @return a copy of this data file, without lower bounds, upper bounds, value counts,
   *         null value counts, or nan value counts
   */
  F copyWithoutStats();
}