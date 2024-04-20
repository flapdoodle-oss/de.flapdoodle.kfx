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

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.StackPane

open abstract class AbstractHeaderColumn<T: Any>(
  internal open val column: Column<T, out Any>,
  val cssClassName: String,
) : StackPane() {

  private val background = WeightGridPane()
  private val contentPane = StackPane()

  private val content = SimpleObjectProperty<Node>()

  init {
    isFocusTraversable = false

    cssClassName(cssClassName)
    background.cssClassName("background")
    Styles.Readonly.set(this, !column.editable)

    children.add(background)
    WeightGridPane.setPosition(contentPane, 0, 0, HPos.CENTER, VPos.CENTER)
    background.children.add(contentPane)
    
    content.addListener { observable, oldValue, newValue ->
      if (oldValue!=null) contentPane.children.remove(oldValue)
      if (newValue!=null) contentPane.children.add(newValue)
    }
  }

  fun setContent(node: Node?) {
    content.value = node
  }
}
