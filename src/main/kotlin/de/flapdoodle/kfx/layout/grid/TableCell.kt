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

data class TableCell<T, N: Node>(
  val node: N,
  val update: (N, T) -> Unit = { _, _ -> }
) {
  fun updateCell(value: T) {
    update(node, value)
  }

  fun initializedWith(value: T): TableCell<T, N> {
    updateCell(value)
    return this
  }

  companion object {
    @Deprecated("too complicated")
    fun <T, N: Node, V> with(node: N, mapper: (T) -> V?, update: (N, V?) -> Unit): TableCell<T, N> {
      return with(node).map(mapper).updateWith(update)
    }

    fun <N: Node> with(node: N) = WithNode(node)

    class WithNode<N: Node>(private val node: N) {
      fun <T> updateWith(update: (N, T?) -> Unit): TableCell<T, N> {
        return TableCell(node, update)
      }

      fun <T, M> map(mapper: (T) -> M): WithMapper<T, N, M> {
        return WithMapper(node, mapper)
      }
    }

    class WithMapper<T, N: Node, V>(private val node: N, private val mapper: (T) -> V) {
      fun  updateWith(update: (N, V?) -> Unit): TableCell<T, N> {
        return TableCell(node) { n, t -> update(n, t?.let(mapper)) }
      }
    }
  }
}