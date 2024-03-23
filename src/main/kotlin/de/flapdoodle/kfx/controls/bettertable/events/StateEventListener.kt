package de.flapdoodle.kfx.controls.bettertable.events

class StateEventListener<T: Any>(
  internal val start: State<T>
): TableRequestEventListener<T> {
  var current = start
  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
//    println("${current}: $event")
    current = current.onEvent(event)
//    println("is now: $current")
  }
}