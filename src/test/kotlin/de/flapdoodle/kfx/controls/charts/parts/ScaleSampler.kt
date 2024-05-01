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

class ScaleSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val now = LocalDate.now()

      //      val content = ColorableLineChart<Number, Number>(x, y, series) {
//        it -> colorMap[it.name] ?: Color.BLACK
//      }

      val range = SimpleObjectProperty(RangeFactories.number(Double::class).rangeOf(listOf(0.0, 100.0)))

      val all = BorderPane().apply {
        left = Scale(range, Direction.LEFT)
        right = Scale(range, Direction.RIGHT)
        top = Scale(range, Direction.TOP)
        bottom = Scale(range, Direction.BOTTOM)
      }

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