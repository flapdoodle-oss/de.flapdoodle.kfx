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
package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.VBox

class SlimRows<T : Any>(
  private val rows: ObservableList<T>,
  private val columns: ObservableList<out Column<T, out Any>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  internal val changeListener: CellChangeListener<T>
) : Control() {
  private val skin = SmartRowsSkin(this)

  init {
    cssClassName("slim-rows")
    isFocusTraversable = false
  }

  override fun createDefaultSkin() = skin

  class SmartRowsSkin<T : Any>(
    private val control: SlimRows<T>
  ) : SkinBase<SlimRows<T>>(control) {
    private val rowPane = VBox()

    init {
      children.add(rowPane)

      ObservableLists.syncWithIndexed(control.rows, rowPane.children) { index, it ->
        SlimRow(control.columns, it, index, control.columnWidthProperties, control.changeListener)
      }
    }
  }

}