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

import javafx.scene.control.Label

fun interface HeaderColumnFactory<T : Any> {
  fun headerColumn(column: Column<T, out Any>): HeaderColumn<T>

  fun andThen(action: (Column<T, out Any>, HeaderColumn<T>) -> Unit): HeaderColumnFactory<T> {
    val that: HeaderColumnFactory<T> = this
    return HeaderColumnFactory<T> {
      val headerColumn = that.headerColumn(it)
      action(it, headerColumn)
      headerColumn
    }
  }

  class Default<T : Any> : HeaderColumnFactory<T> {
    override fun headerColumn(column: Column<T, out Any>): HeaderColumn<T> {
      return HeaderColumn(column).apply {
        setContent(Label(column.label))
      }
    }
  }
}