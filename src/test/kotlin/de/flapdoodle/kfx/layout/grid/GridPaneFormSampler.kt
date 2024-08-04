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
package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.controls.bettertable.Column
import de.flapdoodle.kfx.controls.bettertable.ColumnProperty
import de.flapdoodle.kfx.controls.bettertable.Table
import de.flapdoodle.kfx.controls.bettertable.TableChangeListener
import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.css.bindCss
import de.flapdoodle.reflection.TypeInfo
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.stage.Stage
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class GridPaneFormSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val rows = SimpleObjectProperty(
        listOf(
          Row("a", 20, 1.87, BigDecimal.ONE),
          Row("very loooooooong", 20, 1.87, BigDecimal(1.0).divide(BigDecimal(3.0), MathContext.DECIMAL128)),
        )
      )

      val columns: SimpleObjectProperty<List<Column<Row, out Any>>> = SimpleObjectProperty(
        listOf(
          Column<Row, String>("Name", ColumnProperty(TypeInfo.of(String::class.java), { it.name }), false),
          Column<Row, Int>("Age", ColumnProperty(TypeInfo.of(Int::class.javaObjectType), { it.age }), false),
          Column<Row, Double>("Size", ColumnProperty(TypeInfo.of(Double::class.javaObjectType), { it.size }), false),
          Column<Row, BigDecimal>("Big", ColumnProperty(TypeInfo.of(BigDecimal::class.java), { it.big }), false)
        )
      )

      stage.scene = Scene(GridPane().apply {
        bindCss(GridPaneFormSampler::class,"sampler")
//        padding = Insets(60.0)

        verticalSpace.set(10.0)

        columnWeights(0.0, 1.0)
        var row = 0
        add(Label("A"), Pos(0, row))
        add(TextField(), Pos(1, row))

        row++
        add(Label("B"), Pos(0, row))
        add(TextField(), Pos(1, row))

//        row++
//        add(Table(
//          rows = rows,
//          columns = columns,
//          stateFactory = { ReadOnlyState(it) },
//          changeListener = TableChangeListener.readOnly()
//        ), Pos(0, row, columnSpan = 2))

        row++
        add(Table(
          rows = rows,
          columns = columns,
          stateFactory = { ReadOnlyState(it) },
          changeListener = TableChangeListener.readOnly()
        ).apply {
//          maxWidth = 200.0
          prefWidth = 200.0
        }, Pos(0, row, columnSpan = 2))

        row++
        add(TextArea("what a world"), Pos(0, row, columnSpan = 2))
      })
      stage.show()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }

  data class Row(val name: String, val age: Int, val size: Double, val big: BigDecimal)
}