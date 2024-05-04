package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.controls.charts.ranges.RangeFactories
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.types.Direction
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.util.*

class SingleScaleSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val range = SimpleObjectProperty(RangeFactories.number(Double::class).rangeOf(listOf(0.0, 10.0)))
      val converter = Converters.validatingFor(Double::class, Locale.GERMANY)

      val all = BorderPane().apply {
        top = Scale(converter, range, Direction.TOP)
        left = Scale(converter, range, Direction.LEFT)
//        bottom = Scale(converter, range, Direction.BOTTOM)
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