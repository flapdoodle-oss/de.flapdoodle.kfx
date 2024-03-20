/**
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

import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region
import javafx.stage.Stage

class WeightGridPaneSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      stage.scene = Scene(WeightGridPane().apply {
        verticalSpace.set(10.0)
        horizontalSpace.set(20.0)

        children.add(elementAt("test", 0, 0).also {
          it.minWidth = 20.0
          it.maxWidth = 100.0
        })
        children.add(elementAt("test-1", 1, 0, horizontalPosition = HPos.RIGHT))
        children.add(elementAt("test-11", 1, 1).also {
          it.maxHeight = 100.0
        })
        children.add(WeightGridPane().apply {
          WeightGridPane.setPosition(this, 0, 1)
          val that = this

          verticalSpace.set(10.0)
          horizontalSpace.set(20.0)

          that.children.add(elementAt("(i) test", 0, 0).also {
            it.minWidth = 20.0
            it.maxWidth = 100.0
          })
          that.children.add(elementAt("(i) test-1", 1, 0, horizontalPosition = HPos.RIGHT))
          that.children.add(elementAt("(i) test-11", 1, 1).also {
            it.maxHeight = 100.0
          })
        })

        setColumnWeight(0, 1.0)
        setColumnWeight(1, 2.0)
        setRowWeight(0, 4.0)
        setRowWeight(1, 1.0)
      })
      stage.show()
    }
    
    private fun elementAt(
      label: String,
      column: Int,
      row: Int,
      horizontalPosition: HPos? = null,
      verticalPosition: VPos? = null
    ): Region {
//      val pane = AnchorPane(Button(label).withAnchors(all = 0.0))
      val button = Button(label)
      WeightGridPane.setPosition(button, column, row, horizontalPosition, verticalPosition)
      return button
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}