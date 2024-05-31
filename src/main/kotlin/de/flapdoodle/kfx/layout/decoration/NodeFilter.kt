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
package de.flapdoodle.kfx.layout.decoration

import javafx.scene.Node
import kotlin.reflect.KClass

fun interface NodeFilter<C : Node> {
  fun filter(nodes: List<Node>): C?

  fun and(other: NodeFilter<C>): NodeFilter<C> {
    val that = this
    return NodeFilter<C> { list ->
      val result = that.filter(list)
      result?.let { other.filter(listOf(it)) }
    }
  }

  companion object {
    fun <C : Node> isInstance(nodeType: KClass<C>): NodeFilter<C> {
      return isInstance(nodeType.java)
    }

    fun <C : Node> isInstance(nodeType: Class<C>): NodeFilter<C> {
      return object : NodeFilter<C> {
        override fun filter(list: List<Node>): C? {
          return list.filterIsInstance(nodeType).firstOrNull()
        }

        override fun toString(): String {
          return "IsInstanceOf($nodeType)"
        }
      }
    }
  }
}