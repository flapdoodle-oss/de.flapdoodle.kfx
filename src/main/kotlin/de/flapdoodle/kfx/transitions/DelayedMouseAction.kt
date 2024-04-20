/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.transitions

import javafx.animation.Transition
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.util.Duration
import javafx.util.Subscription

@Deprecated("put stuff like that in an event handler")
class DelayedMouseAction(
  triggerDelay: Duration,
  val action: () -> Unit,
  val undo: (() -> Unit)?
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
      undo?.invoke()
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
            if (!delayedAction.finished) {
              delayedAction.playFromStart()
            }
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