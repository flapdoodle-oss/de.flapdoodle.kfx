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

import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.bindCss
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
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
        onAction = EventHandler {
          println("Action!!")
        }
      }

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        visibleProperty()
        bindCss(ValidatingColoredTextFieldSampler::class,"sampler")
        children.add(intField)
        children.add(ValidatedLabel(converter).apply {
          valueProperty().bind(intField.valueProperty())
        })
        children.add(TextField("Foo").apply {
          tooltip = Tooltips.tooltip("FooBar")
        })
        children.add(Button("OK").apply {
          onAction = EventHandler {
            intField.set(123565)
          }
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