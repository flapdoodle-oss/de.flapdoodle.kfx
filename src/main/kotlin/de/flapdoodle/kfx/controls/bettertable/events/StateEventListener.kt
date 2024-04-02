package de.flapdoodle.kfx.controls.bettertable.events

class StateEventListener<T: Any>(
  internal val start: State<T>
): TableRequestEventListener<T> {
  var current = start
  val debug = true
  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
    if (debug) println("-----------------------------------------")
    if (debug) println("${current}: $event")
    current = current.onEvent(event)
    if (debug) println("is now: $current")
  }
}