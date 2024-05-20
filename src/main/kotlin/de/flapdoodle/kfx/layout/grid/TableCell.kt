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
package de.flapdoodle.kfx.layout.grid

import javafx.scene.Node

data class TableCell<T: Any, N: Node>(
  val node: N,
  val update: (N, T) -> Unit = { node, value -> }
) {
  fun updateCell(value: T) {
    update(node, value)
  }

  companion object {
    fun <T: Any, N: Node, V: Any> with(node: N, mapper: (T) -> V?, update: (N, V?) -> Unit): TableCell<T, N> {
      return TableCell(node) { n, v -> update(n, mapper(v)) }
    }
  }
}