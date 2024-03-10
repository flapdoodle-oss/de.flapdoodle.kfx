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

      val columns = SimpleObjectProperty<List<Column<String, out Any>>>(listOf(
        Column(
          header = { Label("A") },
          cell = {
            Cell(
              value = 1,
              converter = Converters.converterFor(Int::class),
            )
          },
          footer = { Label("a") }
      )))

      val content = AnchorPane(
        Table(columns).withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value = columns.value + Column(
              header = { Label("B")},
              cell = { Cell("", Converters.converterFor(String::class))}
            )
          }
        }, Button("C").apply {
            onAction = EventHandler {
              columns.value = columns.value + Column(
                header = { Label("C")},
                cell = { Cell("", Converters.converterFor(String::class))},
                footer = { Label("c")}
              )
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