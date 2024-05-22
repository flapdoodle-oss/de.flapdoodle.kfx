package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.converters.Converters
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.util.*

class ValidatingColoredTextFieldSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val converter = Converters.validatingFor(Int::class, Locale.GERMANY)

      val intField = ValidatingColoredTextField(
        converter = converter,
        mapColors = { i, s ->
          if ((s?.length ?: 0) > 3) {
            listOf(ColoredLabel.Part(2,4, Color.RED))
          } else
            emptyList()
        }
      ).apply {
        valueProperty().addListener { _, _, newValue ->
          println("1: --> $newValue")
        }
      }

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(intField)
        children.add(ValidatedLabel(converter).apply {
          valueProperty().bind(intField.valueProperty())
        })
        children.add(Button("OK"))
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