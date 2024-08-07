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
package de.flapdoodle.kfx.controls.bettertable

interface TableChangeListener<T: Any> {
  fun changeCell(row: T, change: CellChange<T, out Any>): ChangedRow<T>

  fun updateRow(row: T, changed: T, errors: List<CellError<T, out Any>>)
  fun removeRow(row: T)
  fun insertRow(index: Int, row: T): Boolean
  fun emptyRow(index: Int): T

  data class CellChange<T: Any, C: Any>(val column: Column<T, C>, val value: C?, val localizedError: String?)
  data class CellError<T: Any, C: Any>(val column: Column<T, C>, val localizedError: String)

  data class ChangedRow<T: Any>(val row: T, val errors: List<CellError<T, out Any>> = emptyList()) {
    fun hasNoErrors(): Boolean = errors.isEmpty()
  }

  companion object {

    fun <T: Any> readOnly() = ReadOnly<T>()
    
    class ReadOnly<T: Any> : TableChangeListener<T> {
      override fun changeCell(row: T, change: CellChange<T, out Any>): ChangedRow<T> {
        TODO("Not yet implemented")
      }

      override fun updateRow(row: T, changed: T, errors: List<CellError<T, out Any>>) {
        TODO("Not yet implemented")
      }

      override fun removeRow(row: T) {
        TODO("Not yet implemented")
      }

      override fun insertRow(index: Int, row: T): Boolean {
        TODO("Not yet implemented")
      }

      override fun emptyRow(index: Int): T {
        TODO("Not yet implemented")
      }

    }
  }
}