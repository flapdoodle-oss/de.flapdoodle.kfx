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

import de.flapdoodle.kfx.controls.labels.ValidatedLabel
import de.flapdoodle.kfx.converters.ReadOnlyStringConverter
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import java.util.*

class ValidatingComboBoxSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val locales = Locale.getISOCountries().map { Locale("", it) }

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        val testee = ValidatingComboBox<Locale>(
          values = locales,
          default = Locale.getDefault(),
          initialConverter = ReadOnlyStringConverter.with { it -> it?.displayName ?: "--" },
          validate = { null }
        )

        val result = ValidatedLabel<Locale>(ReadOnlyStringConverter.with { it -> it?.displayName ?: "--" })
        
        children.add(testee)
        children.add(result)
        children.add(Button("OK"))
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