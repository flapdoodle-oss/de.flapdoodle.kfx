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

import de.flapdoodle.kfx.bindings.ObjectBindings
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.Window

class Wizard<T : Any, C>(
  initial: T?,
  private val factories: List<DialogContentFactory<T, C>>
) : Dialog<T>()
    where C : Node,
          C : DialogContent<T> {

  private var currentStep = 0
  private var stateList = emptyList<Pair<T?, C>>()

  init {
    require(factories.isNotEmpty()) { "no factories" }
    isResizable = true

    val next = initial to factories[currentStep].create(initial)
    stateList = stateList + next

    updateNavigation()

    setResultConverter { dialogButton: ButtonType? ->
      val current = stateList[stateList.size-1]
      if (dialogButton?.buttonData == ButtonData.OK_DONE) {
        current.second.result()
      } else
        current.second.abort()
    }
  }

  private fun updateNavigation() {
    dialogPane.buttonTypes.clear()
    dialogPane.buttonTypes.addAll(ButtonType.CANCEL)
    
    if (currentStep + 1 < factories.size) {
      dialogPane.buttonTypes.addAll(ButtonType.NEXT)
    } else {
      dialogPane.buttonTypes.addAll(ButtonType.OK)
    }
    if (currentStep > 0) {
      dialogPane.buttonTypes.addAll(ButtonType.PREVIOUS)
    }

    val current = stateList[stateList.size-1].second
    current.enter()
    title = current.title()
    dialogPane.content = current

    val okButton = dialogPane.lookupButton(ButtonType.OK)
    val nextButton = dialogPane.lookupButton(ButtonType.NEXT)
    val prevButton = dialogPane.lookupButton(ButtonType.PREVIOUS)

    if (nextButton != null) {
      nextButton.disableProperty().bind(negate(current.isValidProperty()))
      nextButton.addEventFilter(ActionEvent.ACTION) { event ->
        event.consume()

        currentStep++
        val nextState = current.result()

        val next = nextState to factories[currentStep].create(nextState)
        stateList = stateList + next
        updateNavigation()
      }
    } else {
      okButton.disableProperty().bind(negate(current.isValidProperty()))
    }
    
    if (prevButton != null) {
      prevButton.addEventFilter(ActionEvent.ACTION) { event ->
        event.consume()

        stateList = stateList.subList(0, currentStep)
        currentStep--
        updateNavigation()
      }
    }

    dialogPane.scene.window.sizeToScene()
  }

  private fun negate(validProperty: ObservableValue<Boolean>): ObservableValue<Boolean> {
    return ObjectBindings.map(validProperty) { it.not() }
  }

  companion object {

    fun <T : Any, C> open(initial: T?, factory: DialogContentFactory<T, C>, vararg factories: DialogContentFactory<T, C>): T?
        where C : Node, C : DialogContent<T> {
      val dialog = Wizard(initial, listOf(factory) + listOf(*factories))
      return dialog.showAndWait()
        .orElse(null)
    }

    fun <T : Any, C> open(window: Window, initial: T?, factory: DialogContentFactory<T, C>, vararg factories: DialogContentFactory<T, C>): T?
        where C : Node, C : DialogContent<T> {
      val dialog = Wizard(initial, listOf(factory) + listOf(*factories))
      dialog.initOwner(window)
      return dialog.showAndWait().orElse(null)
    }
  }
}