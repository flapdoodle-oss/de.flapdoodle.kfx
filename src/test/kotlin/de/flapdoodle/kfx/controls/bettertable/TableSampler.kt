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

import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.time.LocalDate

class TableSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val toggle = SimpleObjectProperty(false)
      val columns = TableFactory.columns()
      val rows = TableFactory.rows()

      val splitPane = SplitPane(
        TableFactory.table(rows, columns, toggle),
        TableFactory.table(rows, columns, toggle) { ReadOnlyState(it) },
      )

      val content = AnchorPane(
        splitPane.withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value += TableFactory.CustomColumn(
              label = "Name",
              property = ColumnProperty(String::class, { it.name }),
              editable = true,
              setter = { row, v, error -> row.copy(name = v).withError("name", error) },
              error = { row -> row.error("name") }
            )
          }
        }, Button("B(r)").apply {
          onAction = EventHandler {
            columns.value += TableFactory.CustomColumn(
              label = "Name*",
              property = ColumnProperty(String::class, { it.name }),
              editable = false,
              setter = { row, v, error -> row.copy(name = v).withError("name", error) },
              error = { row -> row.error("name") }
            )
          }
        }, Button("C").apply {
          onAction = EventHandler {
            columns.value += TableFactory.CustomColumn(
              label = "Size",
              property = ColumnProperty(Double::class, { it.size }),
              editable = true,
              setter = { row, v, error -> row.copy(size = v).withError("size", error) },
              error = { row -> row.error("size") }
            )
          }
        }, Button("C(r)").apply {
          onAction = EventHandler {
            columns.value += Column(
              label = "Size*",
              property = ColumnProperty<TableFactory.Row, Double>(Double::class, { it.size }),
              editable = false
            )
          }
        }, Button("D").apply {
          onAction = EventHandler {
            columns.value += TableFactory.CustomColumn(
              label = "BDay",
              property = ColumnProperty(LocalDate::class, { it.birthDay }),
              editable = true,
              setter = { row, v, error -> row.copy(birthDay = v).withError("birthDay", error) },
              error = { row -> row.error("birthDay") }
            )
          }
        }, Button("toggle").apply {
          onAction = EventHandler {
            toggle.value = !toggle.value
          }
        }, Button("-").apply {
          onAction = EventHandler {
            if (columns.value.size>0) {
              columns.value = columns.value.subList(0, columns.value.size - 1)
            }
          }
        }, Button("delete all").apply {
          onAction = EventHandler {
            rows.value = emptyList()
          }
        }, Button("+").apply {
          onAction = EventHandler {
            rows.value = rows.value + TableFactory.Row(rows.value.size + 20, "Peter", 1.78, LocalDate.now())
          }
        }).withAnchors(bottom = 0.0)
      )
      stage.scene = Scene(content, 800.0, 600.0)
      stage.show()
    }

  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}