package de.flapdoodle.kfx.controls.labels

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import javafx.stage.Stage

class ColoredLabelSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(Label("Foo"))
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
