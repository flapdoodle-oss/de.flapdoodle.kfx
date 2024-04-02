package de.flapdoodle.kfx.controls.bettertable.events

class StateEventListener<T: Any>(
  internal val start: State<T>
): TableRequestEventListener<T> {
  var current = start
  val debug = false

  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
    if (debug) println("-----------------------------------------")
    if (debug) println("${current}: $event")
//    try {
      current = current.onEvent(event)
//    } catch (ex: IllegalArgumentException) {
//      throw RuntimeException("something went wrong bc of", event.cause).apply {
//        addSuppressed(ex)
//      }
//    }
    if (debug) println("is now: $current")
  }
}