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

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.converters.Converters
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.time.LocalDate

@ExtendWith(ApplicationExtension::class)
class CellIT {

  private val row = TableFactory.Row(12, null, null)
  private val column: Column<TableFactory.Row, Int> = Column(
    label = "label",
    property = ColumnProperty(Int::class, TableFactory.Row::age, Converters.converterFor(Int::class)),
    editable = true
  )
  private var events = emptyList<TableEvent.RequestEvent<TableFactory.Row>>()

  @Start
  private fun createElement(stage: Stage) {
    val pane = Pane().apply {
      id = "pane"
    }
    stage.scene = Scene(pane, 200.0, 200.0)
    stage.show()
  }


  @Test
  fun singleClickAndDoubleClickEvents(robot: FxRobot) {
    val pane = robot.lookup<Node> { it.id == "pane" }.queryAs(Pane::class.java)
    val testee = Cell(column, row, 12, { event ->
      events = events + event
    })
    robot.interact {
      pane.children.add(testee)
    }

    events = emptyList()

    robot.clickOn(testee, MouseButton.PRIMARY)

    assertThat(events)
      .hasSize(1)
      .containsExactly(TableEvent.RequestFocus(row, column))

    events = emptyList()

    robot.clickOn(testee, MouseButton.PRIMARY)

    assertThat(events)
      .hasSize(1)
      .containsExactly(TableEvent.RequestEdit(row, column))

  }

  @Test
  fun preserveDimension(robot: FxRobot) {
    val pane = robot.lookup<Node> { it.id == "pane" }.queryAs(Pane::class.java)
    val testee = Cell(column, row, 12, { event ->
      events = events + event
    })
    robot.interact {
      pane.children.add(testee)
    }

//    val testee = robot.lookup(Predicate { it.id=="testee" }).queryAs(Cell::class.java) as Cell<TableFactory.Row, Int>
//    val label = robot.lookup<Node> { it.parent?.parent?.id == "testee" && it is Label }.queryAs(Label::class.java)
//    val field = robot.lookup<Node> { it.parent?.parent?.id == "testee" && it is FieldWrapper<*,*> }.queryAs(FieldWrapper::class.java)
//    val anchorPane = robot.lookup<Node> { it.parent?.id == "testee" }.queryAs(AnchorPane::class.java)

    val testeeBounds = testee.layoutBounds
//    val labelBounds = label.layoutBounds
//    val fieldBounds = field.layoutBounds
//    val anchorBounds = anchorPane.layoutBounds

//    Thread.sleep(1000)
//
//    field.isVisible = true
//
//    Thread.sleep(1000)
//
//    field.isVisible = false
//    println("label: $label")
//
//
    robot.interact {
      testee.onTableEvent(TableEvent.StartEdit(row, column))
    }

    robot.interact {
      testee.onTableEvent(TableEvent.StopEdit(row, column))
    }

//    Thread.sleep(1000)

    val testeeAfterEdit = testee.layoutBounds
//    val labelAfterEdit = label.layoutBounds
//    val fieldAfterEdit = field.layoutBounds
//    val anchorAfterEdit = anchorPane.layoutBounds

//    println("testee: $testeeBounds -> $testeeAfterEdit")
//    println("label: $labelBounds -> $labelAfterEdit")
//    println("field: $fieldBounds -> $fieldAfterEdit")
//    println("wrapper: $anchorBounds -> $anchorAfterEdit")

    assertThat(testeeBounds).isEqualTo(testeeAfterEdit)
  }

  @Test
  fun columnSizeMinMustNotChange(robot: FxRobot) {
    val pane = robot.lookup<Node> { it.id == "pane" }.queryAs(Pane::class.java)

    val localDateColumn = Column(
      label = "X",
      property = ColumnProperty(LocalDate::class, TableFactory.Row::birthDay, Converters.converterFor(LocalDate::class)),
      editable = true
    )

    robot.interact {
      val testee = Cell(localDateColumn, row, null, { event ->
        events = events + event
      })
      pane.children.add(testee)
      val columnSizeWithoutValue = testee.columnSize()

      pane.children.remove(testee)

      val testeeWithValue = Cell(localDateColumn, row, LocalDate.now(), { event ->
        events = events + event
      })
      pane.children.add(testeeWithValue)
      val columnSizeWithValue = testeeWithValue.columnSize()

//      println("$columnSizeWithoutValue -> $columnSizeWithValue")
      assertThat(columnSizeWithoutValue.min).isEqualTo(columnSizeWithValue.min)
      assertThat(columnSizeWithoutValue.preferred).isLessThanOrEqualTo(columnSizeWithValue.preferred)
    }
  }
}