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

import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.ScrollPane
import javafx.scene.control.SkinBase
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

class SlimTable<T: Any>(
  internal val rows: ObservableList<T>,
  internal val columns: ObservableList<out Column<T, out Any>>,
  internal val changeListener: CellChangeListener<T>
) : Control() {
  init {
    isFocusTraversable = false
    bindCss("slim-table")
  }

  private val skin = Skin(this)

  override fun createDefaultSkin() = skin
  fun columns() = columns

  inner class Skin<T : Any>(
    private val control: SlimTable<T>
  ) : SkinBase<SlimTable<T>>(control) {

    private val header = SlimHeader(control.columns)
    private val rowsPane = SlimRows(control.rows, control.columns, header::columnWidthProperty, control.changeListener).apply {
      VBox.setVgrow(this, Priority.ALWAYS)
    }
    private val footer = SlimFooter(control.columns, header::columnWidthProperty)

//    private val wrapper = ScrollPane().apply {
//      hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
//      vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
//    }

    private val scroll = ScrollPane().apply {
      hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
      content = rowsPane
    }

    private val all = WeightGridPane().apply {
      setRowWeight(0, 0.0)
      setRowWeight(1, 1.0)
      setRowWeight(2, 0.0)
      WeightGridPane.setPosition(header, 0, 0)
      WeightGridPane.setPosition(scroll, 0, 1)
      WeightGridPane.setPosition(footer, 0, 2)
      children.add(header)
      children.add(scroll)
      children.add(footer)
    }

    init {
      children.add(all)
    }
  }

}