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