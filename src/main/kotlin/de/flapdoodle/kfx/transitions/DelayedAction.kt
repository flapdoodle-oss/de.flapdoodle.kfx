package de.flapdoodle.kfx.transitions

import javafx.animation.Transition
import javafx.event.EventHandler
import javafx.util.Duration

class DelayedAction(
  triggerDelay: Duration,
  val action: () -> Unit
) : Transition() {

  init {
    cycleDuration = Duration.millis(1.0)
    cycleCount = 1
    delay = triggerDelay
    onFinished = EventHandler {
      action()
    }
  }
  override fun interpolate(frac: Double) {
    
  }
}