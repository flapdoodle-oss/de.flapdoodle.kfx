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
package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.converters.impl.LocalDateConverter
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import java.time.LocalDate
import java.util.*

class ValidatingTextFieldSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val intField = ValidatingTextField(Converters.validatingFor(Int::class, Locale.GERMANY)).apply {
        valueProperty().addListener { _, _, newValue ->
          println("--> $newValue")
        }
//        lastExceptionProperty().addListener { _, _, ex ->
//          if (ex != null) {
//
//            tooltip = Tooltip(ex.localizedMessage)
//            border = Border.stroke(Color.RED)
////              background = Background.fill(Color.RED)
//          } else {
//            tooltip = null
//            border = null
////              background = null
//          }
//        }
      }

      val doubleField = ValidatingTextField(Converters.validatingFor(Double::class, Locale.GERMANY)).apply {
        valueProperty().addListener { _, _, newValue ->
          println("--> $newValue")
        }
//        lastExceptionProperty().addListener { _, _, ex ->
//          if (ex != null) {
//            border = Border.stroke(Color.RED)
//          } else {
//            border = null
//          }
//        }
      }

      val dateField = ValidatingDatePicker(LocalDateConverter(Locale.GERMANY)).apply {
        valueProperty().addListener { _, _, newValue ->
          println("--> $newValue")
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
        children.add(dateField)
        children.add(TypedLabel(LocalDate::class).apply {
          valueProperty().bind(dateField.valueProperty())
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
