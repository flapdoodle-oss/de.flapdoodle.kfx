package de.flapdoodle.kfx.controls.bettertable

fun interface TableRequestEventListener<T: Any> {
  fun fireEvent(event: TableEvent.RequestEvent<T>)
}