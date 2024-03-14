package de.flapdoodle.kfx.controls.bettertable.events

interface State<T: Any> {
  fun onEvent(event: TableEvent.RequestEvent<T>): State<T>
}