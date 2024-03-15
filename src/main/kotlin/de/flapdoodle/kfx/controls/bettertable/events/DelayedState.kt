package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.transitions.DelayAction
import javafx.util.Duration

class DelayedState<T : Any>(
  private val base: State<T>,
  private val delayedState: () -> State<T>,
) : State<T> {
  private val delayAction = DelayAction(Duration.millis(700.0))
  private var current = base

  init {
    delayAction.call {
      current = delayedState()
    }
  }

  override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
    delayAction.stop()
    return current.onEvent(event)
  }
}