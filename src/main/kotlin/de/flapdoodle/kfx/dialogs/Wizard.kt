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
  val factories: List<WizardContentFactory<T, C>>
) : Dialog<T>()
    where C : Node,
          C : WizardContent<T> {

  private var currentStep = 0
  private var stateList = emptyList<Pair<T?, C>>()

  init {
    require(factories.isNotEmpty()) { "no factories" }
    isResizable = true

    val next = initial to factories[currentStep].create(initial)
    stateList = stateList + next

    updateNavigation()

    setResultConverter { dialogButton: ButtonType? ->
      println("pressed: $dialogButton")
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

    fun <T : Any, C> open(inital: T?, factory: WizardContentFactory<T, C>, vararg factories: WizardContentFactory<T, C>): T?
        where C : Node, C : WizardContent<T> {
      val dialog = Wizard(inital, listOf(factory) + listOf(*factories))
      return dialog.showAndWait()
        .orElse(null)
    }

    fun <T : Any, C> open(window: Window, inital: T?, factory: WizardContentFactory<T, C>, vararg factories: WizardContentFactory<T, C>): T?
        where C : Node, C : WizardContent<T> {
      val dialog = Wizard(inital, listOf(factory) + listOf(*factories))
      dialog.initOwner(window)
      return dialog.showAndWait().orElse(null)
    }
  }
}