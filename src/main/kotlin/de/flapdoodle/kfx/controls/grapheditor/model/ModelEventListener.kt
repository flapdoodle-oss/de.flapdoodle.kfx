package de.flapdoodle.kfx.controls.grapheditor.model

@FunctionalInterface
fun interface ModelEventListener<T> {
  fun onEvent(event: ModelEvent<T>): Boolean
}