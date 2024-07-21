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

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.css.cssClassName
import de.flapdoodle.kfx.layout.StackLikeRegion
import javafx.beans.value.ObservableValue
import javafx.scene.layout.HBox

class Footer<T : Any>(
  private val columns: ObservableValue<List<Column<T, out Any>>>,
  private val columnWidthProperties: (Column<T, out Any>) -> ObservableValue<Number>,
  private val footerColumnFactory: FooterColumnFactory<T>?
) : StackLikeRegion() {

  private val footer = HBox()

  init {
    isFocusTraversable = false
    cssClassName("footer")

    if (footerColumnFactory!=null) {
      ObservableLists.syncWith(columns, footer.children) {
        val footerColumn = footerColumnFactory.footerColumn(it)
        footerColumn.prefWidthProperty().bind(columnWidthProperties(it))
        footerColumn
      }
    }
    children.add(footer)
  }
}