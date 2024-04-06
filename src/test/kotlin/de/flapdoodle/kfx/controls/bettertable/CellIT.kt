package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.TableEvent
import de.flapdoodle.kfx.controls.fields.FieldWrapper
import de.flapdoodle.kfx.converters.Converters
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class CellIT {

  private val row = TableFactory.Row(12,null, null)
  private val column: Column<TableFactory.Row, Int> = Column(
    label = "label",
    property = ColumnProperty(Int::class, TableFactory.Row::age, Converters.converterFor(Int::class)),
    editable = true
  )
  private var events = emptyList<TableEvent.RequestEvent<TableFactory.Row>>()
  
  @Start
  private fun createElement(stage: Stage) {
    val pane = Pane(Cell(column, row, 12).apply {
      id = "testee"
      setEventListener { event ->
        events = events + event
      }
    })
    stage.scene = Scene(pane, 200.0, 200.0)
    stage.show()
  }


  @Test
  fun singleClickAndDoubleClickEvents(robot: FxRobot) {
    val testee = robot.lookup(Predicate { it.id=="testee" }).queryAs(Cell::class.java) as Cell<TableFactory.Row, Int>
//    robot.doubleClickOn(testee, MouseButton.PRIMARY)
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
    val testee = robot.lookup(Predicate { it.id=="testee" }).queryAs(Cell::class.java) as Cell<TableFactory.Row, Int>
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
}