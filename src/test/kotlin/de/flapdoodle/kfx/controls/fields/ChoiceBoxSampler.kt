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

import de.flapdoodle.kfx.types.Direction
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.StringConverter
import java.util.*
import kotlin.reflect.KClass

class ChoiceBoxSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val choiceBox = ChoiceBoxes.forTypes(
        Locale.GERMANY,
        "testTypes",
        listOf(Int::class, Double::class, String::class)
      )

      val directionBox = ChoiceBoxes.forEnums(
        Locale.GERMANY,
        "testEnums",
        Direction::class
      )

      stage.scene = Scene(FlowPane(Orientation.VERTICAL).apply {
        children.addAll(choiceBox, directionBox)
//        children.add(Button("!").apply {
//          onAction = EventHandler {
////            dateField.error = "what?"
//          }
//        })
//        children.add(Button(":)").apply {
//          onAction = EventHandler {
////            dateField.error = null
//          }
//        })
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