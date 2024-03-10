package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Stage

class TableSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val columns = SimpleObjectProperty<List<Column<Row, out Any>>>(listOf(
        Column(
          header = { Label("A") },
          cell = {
            Cell(
              row = it,
              value = it.age,
              converter = Converters.converterFor(Int::class),
              editable = true
            )
          },
          footer = { Label("a") },
      )))

      val rows = SimpleObjectProperty(listOf(
        Row(20, "Klaus", 1.89),
        Row(30, "Susi", 1.79),
        Row(28, "Peter", 1.67),
        Row(17, "Claudia", 1.93),
        Row(19, "Joane", 1.88),
        Row(44, "Achim", 1.82),
        Row(67, "Thorsten", 1.78),
      ))

      val changeListener = CellChangeListener<Row> { row, change ->
        println("change: $row -> $change")
      }
      
      val content = AnchorPane(
        Table(rows, columns, changeListener).withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value = columns.value + Column(
              header = { Label("B")},
              cell = { Cell(it, it.name, Converters.converterFor(String::class), true)}
            )
          }
        }, Button("C").apply {
            onAction = EventHandler {
              columns.value = columns.value + Column(
                header = { Label("C")},
                cell = { Cell(it, it.size, Converters.converterFor(Double::class), true)},
                footer = { Label("c")}
              )
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

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}