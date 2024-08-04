package de.flapdoodle.kfx.dialogs

import javafx.scene.Node

fun interface WizardContentFactory<T : Any, C>
    where C : Node, C : WizardContent<T> {
  fun create(value: T?): C
}