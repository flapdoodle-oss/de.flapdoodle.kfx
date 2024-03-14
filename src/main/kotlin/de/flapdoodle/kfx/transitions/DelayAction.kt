package de.flapdoodle.kfx.transitions

import javafx.animation.Transition
import javafx.event.EventHandler
import javafx.util.Duration

class DelayAction(val delay: Duration) {
  private var action: () -> Unit = {}
  private val transition = object : Transition() {
    init {
      cycleDuration = Duration.millis(1.0)
      cycleCount = 1
      delay = this@DelayAction.delay
      onFinished = EventHandler {
        action.invoke()
      }
    }

    override fun interpolate(frac: Double) {
      // nothing to do
    }
  }

  fun call(action: () -> Unit) {
    this.action = action
    transition.playFromStart()
  }

  fun stop() {
    transition.stop()
    action = {}
  }

}