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
package de.flapdoodle.kfx.extensions

import javafx.css.PseudoClass
import javafx.scene.Node

class PseudoClassWrapper<T: Node>(
  private val wrapped: PseudoClass
) {
  fun enable(node: T) {
    set(node, true)
  }

  fun enabled(node: T): Boolean {
    return node.pseudoClassStates.contains(wrapped)
  }

  fun disable(node: T) {
    set(node, false)
  }

  fun set(node: T, active: Boolean) {
    node.pseudoClassStateChanged(wrapped, active)
  }

  fun swap(node: T) {
    node.pseudoClassStateChanged(wrapped, !node.pseudoClassStates.contains(wrapped))
  }
}