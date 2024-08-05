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
package de.flapdoodle.kfx.dialogs

import javafx.application.Application
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class WizardSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val flow = VBox().apply {
        children.add(Button("A").apply {
          onAction = EventHandler {
            val result = Wizard.open("Foo", ::Step01)
            println("wizard result: $result")
          }
        })
        children.add(Button("B").apply {
          onAction = EventHandler {
            val result = Wizard.open("Foo", ::Step01, ::Step02)
            println("wizard result: $result")
          }
        })
      }

      stage.scene = Scene(flow, 800.0, 600.0)
      stage.show()
    }

  }

  class Step01(
    val initial: String?
  ) : Pane(), WizardContent<String> {

    private val isValid = SimpleBooleanProperty(false)

    init {
      children.addAll(Button("click $initial").apply {
        onAction = EventHandler {
          isValid.value = true
        }
      })
    }

    override fun title(): String {
      return "Step 01"
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
      return isValid
    }

    override fun result(): String {
      return "01"
    }

  }

  class Step02(
    val value: String?
  ) : Pane(), WizardContent<String> {

    private val isValid = SimpleBooleanProperty(false)

    init {
      children.addAll(Button("click $value").apply {
        onAction = EventHandler {
          isValid.value = true
        }
      })
    }

    override fun title(): String {
      return "Step 02"
    }

    override fun isValidProperty(): ObservableValue<Boolean> {
      return isValid
    }

    override fun result(): String {
      return "02"
    }

  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}