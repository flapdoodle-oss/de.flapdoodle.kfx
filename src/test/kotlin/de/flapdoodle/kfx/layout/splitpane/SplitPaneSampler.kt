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
package de.flapdoodle.kfx.layout.splitpane

import de.flapdoodle.kfx.extensions.withAnchors
import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Stage

class SplitPaneSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val oldSplitPane = BetterSplitPane().apply {
//        background = Background.fill(Color.rgb(240,240,204))
//        border = Border.stroke(Color.BLUE)

        nodes().addAll(LabelContent("A"), ButtonContent("1"), LabelContent("B"))
      }

      val nodes = SimpleObjectProperty(
        listOf(LabelContent("A"), ButtonContent("1"), LabelContent("B"))
      )

      val onDoubleClick = { node: Content ->
        println("clicked on $node")
      }
      val splitPane = SplitPane(nodes, onDoubleClick).apply {
//        background = Background.fill(Color.RED)
//        border = Border.stroke(Color.RED)
      }

      val buttons = SimpleObjectProperty(
        listOf(Label("A"), Button("1"), Label("B"))
      )

//      val splitPane2 = SplitPane(buttons).apply {
////        background = Background.fill(Color.RED)
//        border = Border.stroke(Color.GREEN)
//        WeightGridPane.setPosition(this,1,2, HPos.LEFT, VPos.CENTER)
//      }

      val grid = WeightGridPane().apply {
        setColumnWeight(0, 0.01)
        setColumnWeight(2, 0.01)
      }
      grid.children.add(wrap(oldSplitPane).apply {
        WeightGridPane.setPosition(this,1,0, HPos.LEFT, VPos.CENTER)
      })
      grid.children.add(wrap(splitPane).apply {
        WeightGridPane.setPosition(this,1,1, HPos.LEFT, VPos.CENTER)
      })
//      content.children.add(splitPane2)
//      stuff.children.add(VBox().apply {
//        WeightGridPane.setPosition(this,2,0, HPos.LEFT, VPos.CENTER)
//        children.add(Button("noop").apply {
//        })
//      })
//      stuff.children.add(VBox().apply {
//        WeightGridPane.setPosition(this,0,1, HPos.LEFT, VPos.CENTER)
//        children.add(Button("noop").apply {
//        })
//      })

      val all=AnchorPane()
      all.children.add(grid.withAnchors(all = 10.0, bottom = 50.0))
      all.children.add(HBox().withAnchors(left = 10.0, bottom = 2.0, right = 10.0).apply {
        children.add(Button("+").apply {
          onAction = EventHandler {
            nodes.value = nodes.value + LabelContent("X")
          }
        })
      })
      stage.scene = Scene(all, 800.0, 600.0)
      stage.show()
    }

    fun wrap(node: Node): ScrollPane {
      return ScrollPane(node).apply {
        isFitToHeight = true
      }
    }

  }

  abstract class Content : AnchorPane() {

  }

  class LabelContent(text: String) : Content() {
    init {
      children.add(Label(text).withAnchors(all = 0.0))
    }
  }

  class ButtonContent(text: String) : Content() {
    init {
      children.add(Button(text).withAnchors(all = 0.0))
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}