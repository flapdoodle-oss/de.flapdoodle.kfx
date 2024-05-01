package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.controls.charts.ranges.RangeFactories
import de.flapdoodle.kfx.types.Direction
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.time.LocalDate

class SingleScaleSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val range = SimpleObjectProperty(RangeFactories.number(Double::class).rangeOf(listOf(0.0, 100.0)))
      val all = Scale(range, Direction.RIGHT)

      stage.scene = Scene(all, 800.0, 600.0)
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