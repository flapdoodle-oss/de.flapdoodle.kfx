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
package de.flapdoodle.kfx.controls.bettertable.events

import de.flapdoodle.kfx.logging.Logging
import de.flapdoodle.kfx.transitions.DelayAction
import javafx.util.Duration

class StateEventListener<T: Any>(
  internal val start: State<T>
): TableRequestEventListener<T> {
  private val logger = Logging.logger(StateEventListener::class)
  private val delayAction = DelayAction(Duration.millis(700.0))

  private var current = start

  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
    try {
      delayAction.stop()

      logger.debug { "${current}: $event" }
      val nextState = current.onEvent(event)
      current = nextState.state
      logger.debug { "after onEvent: $current" }
      if (nextState.event!=null) {
        logger.debug { "additional event: ${nextState.event}" }
        fireEvent(nextState.event)
      }
      val delayed = nextState.delayed
      
      if (delayed !=null) {
        delayAction.call {
          current = delayed.state
          logger.debug { "delayed state: $current" }
          if (delayed.event != null) {
            logger.debug { "additional event: ${delayed.event}" }
            fireEvent(delayed.event)
          }
        }
      }
    } finally {
      logger.debug { "is now: $current" }
    }
  }
}