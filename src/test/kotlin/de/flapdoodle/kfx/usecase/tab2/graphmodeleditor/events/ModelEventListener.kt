package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.events

@FunctionalInterface
fun interface ModelEventListener<T> {
  fun onEvent(event: ModelEvent<T>): Boolean
}