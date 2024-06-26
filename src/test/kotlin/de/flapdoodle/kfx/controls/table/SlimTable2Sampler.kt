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

import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter

class SlimTable2Sampler {
  class Sample : Application() {

    private fun rows(size: Int): ObservableList<Data> {
      val ret = FXCollections.observableArrayList<Data>()
      (0..<size).forEach {
        ret.add(Data("Name$it", 10+it))
      }
      return ret
    }

    override fun start(stage: Stage) {
      val toogle = SimpleObjectProperty(false)

      val splitPane = SplitPane()
      splitPane.items.add(StackPane().apply {
        children.add(slimTableSample(rows(0), toogle))
      })
      splitPane.items.add(StackPane().apply {
        children.add(slimTableSample(rows(10), toogle))
      })
      splitPane.items.add(StackPane().apply {
        children.add(slimTableSample(rows(100), toogle))
      })
      splitPane.setDividerPositions(0.33, 0.66)


      val wrapper = AnchorPane()
      wrapper.children.add(splitPane.withAnchors(all = 10.0))
      wrapper.children.add(Button("toggle").apply {
        withAnchors(right = 0.0, top = 0.0)
        onAction = EventHandler {
          toogle.value = !toogle.value
        }
      })

      stage.scene = Scene(wrapper, 800.0, 600.0)
      stage.show()
    }

    private fun slimTableSample(data: ObservableList<Data>, toggle: ObservableValue<Boolean>): Node {
      val backGroundToogled = toggle.map {
        if (it)
          Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
        else null
      }

      val nameColumn = Column<Data, String>(
        header = { Label("name") },
        cell = { it ->
          SlimCell<Data, String>(
            it.name,
            DefaultStringConverter(),
            true
          )
        },
        footer = { Label("N") }
      )

      val ageColumn = Column<Data, Int>(
        header = {
          Label("age").apply {
            this.backgroundProperty().bind(backGroundToogled)
          }
        },
        cell = { it ->
          SlimCell<Data, Int>(
            it.age,
            IntegerStringConverter(),
            true
          ).apply {
            this.backgroundProperty().bind(backGroundToogled)
          }
        },
        footer = { Label("A") }
      )
      val columns = FXCollections.observableArrayList(nameColumn, ageColumn)

      val table = SlimTable<Data>(
        data,
        columns
      ) { row, change ->
        println("change ($row, ${change.column}) to ${change.value}")
        val source: Data = data[row]
        when (change.column) {
          nameColumn -> source.name = change.value as String?
          ageColumn -> source.age = change.value as Int?
        }
        data[row] = source
      }
      return table
    }


    class Data(var name: String?, var age: Int?) {

    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}