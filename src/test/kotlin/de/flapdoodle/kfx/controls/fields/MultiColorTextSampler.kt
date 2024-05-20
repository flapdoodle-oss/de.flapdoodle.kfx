package de.flapdoodle.kfx.controls.fields

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.layout.Border
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Stage


class MultiColorTextSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val flow = TextFlow()
      val log = ">> Sample passed \n"
      val t1: Text = Text()
      t1.setStyle("-fx-fill: #4F8A10;-fx-font-weight:bold;")
      t1.setText(log)
      val t2: Text = Text()
      t2.setStyle("-fx-fill: RED;-fx-font-weight:normal;")
      t2.setText(log)
      flow.children.addAll(t1, t2)

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(flow)
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
