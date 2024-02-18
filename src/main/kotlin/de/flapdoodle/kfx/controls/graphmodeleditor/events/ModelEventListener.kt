package de.flapdoodle.kfx.controls.graphmodeleditor.events

@FunctionalInterface
fun interface ModelEventListener<T> {
  fun onEvent(event: ModelEvent<T>): Boolean
}