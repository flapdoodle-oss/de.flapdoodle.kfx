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
package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.controls.bettertable.TableChangeListener

abstract class StateWithContext<T: Any>(
  private val context: EventContext<T>
) : State<T> {
  protected fun onTableEvent(event: TableEvent.ResponseEvent<T>) {
    context.onTableEvent(event)
  }

  protected fun changeCell(row: T, change: TableChangeListener.CellChange<T, out Any>): TableChangeListener.ChangedRow<T> {
    return context.changeListener.changeCell(row, change)
  }

  protected fun updateRow(row: T, changed: T, errors: List<TableChangeListener.CellError<T, out Any>>) {
    context.changeListener.updateRow(row, changed, errors)
  }

  protected fun insertRow(index: Int, row: T): Boolean {
    return context.changeListener.insertRow(index, row)
  }

  protected fun removeRow(row: T) {
    context.changeListener.removeRow(row)
  }
}