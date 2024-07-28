/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.layout.grid

data class Pos(
  val column: Int,
  val row: Int,
  val columnSpan: Int = 1,
  val rowSpan: Int = 1
) {
  private val lastColumn = column + columnSpan - 1
  private val lastRow = row + rowSpan - 1

  init {
    require(column >= 0) { "column must be non-negative but was $column" }
    require(row >= 0) { "row must be non-negative but was $row" }
    require(columnSpan > 0) { "column span must be one ore more but was $columnSpan" }
    require(rowSpan > 0) { "row span must be one or more but was $rowSpan" }
  }

  fun matchesColumn(column: Int): Boolean {
    return this.column <= column && column <= lastColumn
  }

  fun matchesRow(row: Int): Boolean {
    return this.row <= row && row <= lastRow
  }
}
