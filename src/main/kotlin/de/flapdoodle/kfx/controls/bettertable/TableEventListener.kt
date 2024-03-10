package de.flapdoodle.kfx.controls.bettertable

fun interface TableEventListener<T: Any> {
  fun fireEvent(event: TableEvent.RequestEvent<T>)
}