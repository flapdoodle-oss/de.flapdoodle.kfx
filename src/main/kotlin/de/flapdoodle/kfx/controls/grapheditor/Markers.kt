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
package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.Key
import de.flapdoodle.kfx.extensions.constraint
import javafx.scene.Node

object Markers {
  private val IsDragBar = Key.ofNamedType("dragbar",Boolean::class)
  private val nodeSlot = Key.ofType(VertexSlotId::class)
  private val IsContent = Key.ofNamedType("content", Boolean::class)
  private val IsExcluded = Key.ofNamedType("excluded", Boolean::class)

  fun isDragBar(node: Node): Boolean {
    return node.constraint[IsDragBar] ?: false
  }

  fun markAsDragBar(node: Node) {
    node.constraint[IsDragBar] = true
  }

  fun unmarkAsDragBar(node: Node) {
    node.constraint[IsDragBar] = null
  }

  fun isContent(node: Node): Boolean {
    return node.constraint[IsContent] ?: false
  }

  fun markAsContent(node: Node) {
    node.constraint[IsContent] = true
  }

  fun unmarkAsContent(node: Node) {
    node.constraint[IsContent] = null
  }

  fun isExcluded(node: Node): Boolean {
    return node.constraint[IsExcluded] ?: false
  }

  fun markAsExcluded(node: Node) {
    node.constraint[IsExcluded] = true
  }

  fun unmarkAsExcluded(node: Node) {
    node.constraint[IsExcluded] = null
  }

  fun nodeSlot(node: Node): VertexSlotId? {
    return node.constraint[nodeSlot]
  }

  fun markAsNodeSlot(node: Node, id: VertexSlotId) {
    node.constraint[nodeSlot] = id
  }

  fun unmarkAsNodeSlot(node: Node) {
    node.constraint[nodeSlot] = null
  }


}