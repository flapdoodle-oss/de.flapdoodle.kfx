package de.flapdoodle.kfx.controls.textfields

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.Border
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class TypedTextFieldSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val intField = TypedTextField(Int::class).apply {
        valueProperty().addListener { _, _, newValue ->
          println("--> $newValue")
        }
        lastExceptionProperty().addListener { _, _, ex ->
          if (ex != null) {

//              tooltip = Tooltip(ex.localizedMessage)
            border = Border.stroke(Color.RED)
//              background = Background.fill(Color.RED)
          } else {
//              tooltip = null
            border = null
//              background = null
          }
        }
      }

      val doubleField = TypedTextField(Double::class).apply {
        valueProperty().addListener { _, _, newValue ->
          println("--> $newValue")
        }
        lastExceptionProperty().addListener { _, _, ex ->
          if (ex != null) {
            border = Border.stroke(Color.RED)
          } else {
            border = null
          }
        }
      }
      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(intField)
        children.add(TypedLabel(Int::class).apply {
          valueProperty().bind(intField.valueProperty())
        })
        children.add(doubleField)
        children.add(TypedLabel(Double::class).apply {
          valueProperty().bind(doubleField.valueProperty())
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
