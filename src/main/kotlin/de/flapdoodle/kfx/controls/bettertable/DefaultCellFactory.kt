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

import de.flapdoodle.kfx.controls.bettertable.events.TableRequestEventListener
import de.flapdoodle.kfx.controls.fields.DefaultFieldFactoryLookup
import de.flapdoodle.kfx.controls.fields.FieldFactoryLookup

class DefaultCellFactory<T: Any>(
  val fieldFactoryLookup: FieldFactoryLookup = DefaultFieldFactoryLookup()
) : CellFactory<T> {
  override fun <C : Any> cell(column: Column<T, C>, row: T, eventListener: TableRequestEventListener<T>): Cell<T, C> {
    return Cell(column, row, column.property.getter(row), eventListener, fieldFactoryLookup)
  }
}