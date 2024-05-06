package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.converters.Converters
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.*
import java.util.*

class DatePickerFieldFactorySampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val dateField = DatePickerFieldFactory(Locale.GERMANY).inputFor(
        null,
        commitEdit = { value, error ->
          println("value: $value, error: $error")
        },
        cancelEdit = {
          println("... canceled")
        }
      )

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(dateField)
        children.add(Button("!").apply {
          onAction = EventHandler {
            dateField.error = "what?"
          }
        })
        children.add(Button(":)").apply {
          onAction = EventHandler {
            dateField.error = null
          }
        })
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
}