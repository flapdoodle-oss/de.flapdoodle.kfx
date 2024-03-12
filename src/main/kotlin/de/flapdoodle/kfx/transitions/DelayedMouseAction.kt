package de.flapdoodle.kfx.transitions

import javafx.animation.Transition
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.util.Duration
import javafx.util.Subscription

class DelayedMouseAction(
  triggerDelay: Duration,
  val action: () -> Unit,
  val undo: () -> Unit
) : Transition() {

  var finished = false

  init {
    cycleDuration = Duration.millis(1.0)
    cycleCount = 1
    delay = triggerDelay
    onFinished = EventHandler {
      finished = true
      action()
    }
  }

  override fun interpolate(frac: Double) {
    
  }

  override fun stop() {
    if (finished) {
      undo()
      finished = false
    }
    super.stop()
  }

  companion object {
    fun delay(node: Node, duration: Duration, action: () -> Unit, undo: () -> Unit): Subscription {
      val delayedAction = DelayedMouseAction(duration, action, undo)

      val eventHandler: (event: MouseEvent) -> Unit = {
        when (it.eventType) {
          MouseEvent.MOUSE_ENTERED -> {
            delayedAction.playFromStart()
          }
          MouseEvent.MOUSE_MOVED -> {
            delayedAction.playFromStart()
          }

          MouseEvent.MOUSE_EXITED -> {
            delayedAction.stop()
          }
        }
      }

      node.addEventFilter(MouseEvent.ANY, eventHandler)

      return Subscription {
        delayedAction.stop()
        node.removeEventFilter(MouseEvent.ANY, eventHandler)
      }
    }
  }
}