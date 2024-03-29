package de.flapdoodle.kfx.controls.bettertable.events

fun interface TableRequestEventListener<T: Any> {
  fun fireEvent(event: TableEvent.RequestEvent<T>)
}