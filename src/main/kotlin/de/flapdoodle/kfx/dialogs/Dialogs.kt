package de.flapdoodle.kfx.dialogs

import javafx.scene.Node
import javafx.stage.Window

object Dialogs {
  fun <T : Any, C> open(initial: T?, factory: DialogContentFactory<T, C>): T?
      where C : Node, C : DialogContent<T> {
    return Wizard.open(initial, factory)
  }

  fun <T : Any, C> open(window: Window, initial: T?, factory: DialogContentFactory<T, C>): T?
      where C : Node, C : DialogContent<T> {
    return Wizard.open(window, initial, factory)
  }
}