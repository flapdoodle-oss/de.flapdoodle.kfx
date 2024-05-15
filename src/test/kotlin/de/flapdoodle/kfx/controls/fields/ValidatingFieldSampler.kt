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
import de.flapdoodle.kfx.converters.ValueOrError
import de.flapdoodle.kfx.converters.impl.LocalDateConverter
import de.flapdoodle.kfx.i18n.I18N
import de.flapdoodle.kfx.types.Direction
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import java.util.*

class ValidatingFieldSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val stringField = ValidatingTextField(Converters.validatingFor(String::class, Locale.GERMANY).and {
        // TODO .. funktioniert das?
        it.flatMap { v -> if (v.isBlank()) ValueOrError.Error(IllegalArgumentException("is empty")) else ValueOrError.Value(v) }
      })
      val intField = ValidatingTextField(Converters.validatingFor(Int::class, Locale.GERMANY))
      val doubleField = ValidatingTextField(Converters.validatingFor(Double::class, Locale.GERMANY))
      val dateField = ValidatingDatePicker(LocalDateConverter(Locale.GERMANY))
      val choiceBox = ChoiceBoxes.forEnums(
        resourceBundle = I18N.resourceBundle(Locale.GERMANY, "testEnums"),
        enumType = Direction::class
      )



      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(Label("String"))
        children.add(stringField)
        children.add(Label("Int"))
        children.add(intField)
        children.add(Label("Double"))
        children.add(doubleField)
        children.add(Label("Date"))
        children.add(dateField)
        children.add(Label("Choice"))
        children.add(choiceBox)
        children.add(Button("OK").apply {
          disableProperty().bind(ValidatingField.invalidInputs(stringField, intField, doubleField, dateField, choiceBox))
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
