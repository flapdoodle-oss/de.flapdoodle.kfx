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
package de.flapdoodle.kfx.controls.labels

import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.stage.Stage

class ColoredLabelSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val text = SimpleObjectProperty("This is \nsample text.")
      val parts = SimpleObjectProperty(listOf(
        ColoredLabel.Part(5, 16, Color.RED),
        ColoredLabel.Part(10, 14, Color.GREEN),
        ColoredLabel.Part(2, 4, Color.BLUE),
        ColoredLabel.Part(18, 32, Color.DARKGREEN),
      ))

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.add(ColoredLabel(text, parts))
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
