/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.charts.parts

import de.flapdoodle.kfx.converters.DefaultValidatingConverterFactory
import de.flapdoodle.kfx.types.Direction
import de.flapdoodle.kfx.types.ranges.RangeFactories
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.time.LocalDate
import java.util.*

class ScaleSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {
      val now = LocalDate.now()

      //      val content = ColorableLineChart<Number, Number>(x, y, series) {
//        it -> colorMap[it.name] ?: Color.BLACK
//      }

      val range = SimpleObjectProperty(RangeFactories.number(Double::class).rangeOf(listOf(0.0, 3.0)))
      val converter = DefaultValidatingConverterFactory.converter(Double::class, Locale.GERMANY)

      val all = BorderPane().apply {
        left = Scale(converter, range, Direction.LEFT)
        right = Scale(converter, range, Direction.RIGHT)
        top = Scale(converter, range, Direction.TOP)
        bottom = Scale(converter, range, Direction.BOTTOM)
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