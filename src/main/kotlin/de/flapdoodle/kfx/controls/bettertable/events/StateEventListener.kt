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

class StateEventListener<T: Any>(
  internal val start: State<T>
): TableRequestEventListener<T> {
  var current = start
  val debug = false

  override fun fireEvent(event: TableEvent.RequestEvent<T>) {
    try {
      if (debug) println("-----------------------------------------")
      if (debug) println("${current}: $event")
      val nextState = current.onEvent(event)
      current = nextState.state
      if (debug) println("after onEvent: $current")
      if (nextState.event!=null) {
        if (debug) println("additional event: ${nextState.event}")
        fireEvent(nextState.event)
      }
    } finally {
      if (debug) println("is now: $current")
    }
  }
}