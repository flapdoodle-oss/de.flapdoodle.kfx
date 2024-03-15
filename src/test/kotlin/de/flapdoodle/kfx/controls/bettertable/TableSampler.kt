package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage

class TableSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val toggle = SimpleObjectProperty(false)
      val backGroundToggled = toggle.map {
        if (it)
          Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
        else null
      }

      val columns = SimpleObjectProperty<List<Column<Row, out Any>>>(
        listOf(
          CustomColumn(
            header = { Label("A") },
            cell = {
              Cell(
                row = it,
                value = it.age,
                converter = Converters.converterFor(Int::class),
                editable = true
              )
            },
            setter = { row, v -> row.copy(age = v ?: 0) },
            footer = { Label("a") },
          )
        )
      )

      val rows = SimpleObjectProperty(
        listOf(
          Row(20, "Klaus", 1.89),
          Row(30, "Susi", 1.79),
          Row(28, "Peter", 1.67),
          Row(17, "Claudia", 1.93),
          Row(19, "Joane", 1.88),
          Row(44, "Achim", 1.82),
          Row(67, "Thorsten", 1.78),
        )
      )

      val changeListener = CellChangeListener<Row> { row, change ->
        println("change: $row -> $change")
        val changed = when (val column = change.column) {
          is CustomColumn<out Any> -> {
            column.change(row, change)
          }
          else -> {
            row
          }
        }

        val list = rows.value
        val index = list.indexOf(row)

        rows.value = list.subList(0, index) + changed + list.subList(index+1, list.size)
      }

      val content = AnchorPane(
        Table(rows, columns, changeListener).withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              header = { Label("B").apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              cell = { Cell(it, it.name, Converters.converterFor(String::class), true).apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              setter = { row, v -> row.copy(name = v ?: "") }
            )
          }
        }, Button("C").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              header = { Label("C") },
              cell = { Cell(it, it.size, Converters.converterFor(Double::class), true) },
              setter = { row, v -> row.copy(size = v ?: 1.5) },
              footer = { Label("c") }
            )
          }
        }, Button("toggle").apply {
          onAction = EventHandler {
            toggle.value = !toggle.value
          }
        }).withAnchors(bottom = 0.0)
      )
      stage.scene = Scene(content, 800.0, 600.0)
      stage.show()
    }

  }

  data class Row(
    val age: Int,
    val name: String,
    val size: Double,
  )

  class CustomColumn<C : Any>(
    header: () -> Node,
    cell: (Row) -> Cell<Row, C>,
    val setter: (Row, C?) -> Row,
    footer: (() -> Node)? = null
  ) : Column<Row, C>(header, cell, footer) {
    fun change(row: Row, change: CellChangeListener.Change<Row, out Any>): Row {
      return if (change.column == this) {
        setter(row, change.value as C?)
      } else row
    }

  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}