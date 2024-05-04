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

import de.flapdoodle.kfx.types.ranges.RangeFactories
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