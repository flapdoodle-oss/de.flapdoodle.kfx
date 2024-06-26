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
package de.flapdoodle.kfx.layout.splitpane

import de.flapdoodle.kfx.bindings.syncWith
import de.flapdoodle.kfx.events.SharedLock
import de.flapdoodle.kfx.extensions.scenePosition
import de.flapdoodle.kfx.extensions.screenDeltaToLocal
import de.flapdoodle.kfx.extensions.snappedToPixel
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import kotlin.math.max

class SplitPane<T : Node>(
  internal val nodes: ReadOnlyObjectProperty<List<T>>,
  internal val onDoubleClick: (T) -> Unit = {}
) : Region() {

  private val sharedLock = SharedLock<Handle<*>>()
  private val nodeHandles = FXCollections.observableArrayList<Handle<T>>()

  private val nodeLayer = NodesLayer(nodes, nodeHandles)
  private val handleLayer = HandleLayer(nodeHandles)

  init {
    isFocusTraversable = false

    styleClass.addAll("split-pane")
    stylesheets += javaClass.getResource("SplitPane.css").toExternalForm();

    nodeHandles.syncWith(nodes) { Handle(it) }

    children.add(nodeLayer)
    children.add(handleLayer)

    addEventFilter(MouseEvent.ANY, ::handleMouseEvent)
//    nodeHandles.addListener(ListChangeListener {
//      nodeLayer.requestLayout()
//      handleLayer.requestLayout()
//    })
  }

  fun setSize(node: T, min: Double, preferred: Double) {
    handleLayer.setSize(node, min, preferred)
    nodeLayer.requestLayout()
    handleLayer.requestLayout()
  }

  override fun computeMinHeight(width: Double): Double {
    val height = children.map { it.minHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    return insets.top + height + insets.bottom
  }

  override fun computeMinWidth(height: Double): Double {
    val width = children.map { it.minWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    return insets.left + width + insets.right
  }

  override fun computePrefHeight(width: Double): Double {
    val height = children.map { it.prefHeight(width) }.fold(0.0) { l, r -> max(l, r) }
    return insets.top + height + insets.bottom
  }

  override fun computePrefWidth(height: Double): Double {
    val width = children.map { it.prefWidth(height) }.fold(0.0) { l, r -> max(l, r) }
    return insets.left + width + insets.right
  }

  override fun layoutChildren() {
    layoutChildren(layoutX, layoutY, width, height)
  }

  private fun layoutChildren(_contentX: Double, _contentY: Double, _contentWidth: Double, _contentHeight: Double) {
    val top = insets.top
    val right = insets.right
    val left = insets.left
    val bottom = insets.bottom

    val contentWidth = _contentWidth - left - right
    val contentHeight = _contentHeight - top - bottom

    children.forEach {
      if (it.isManaged) {
        layoutInArea(it, left, top, contentWidth, contentHeight, -1.0, HPos.LEFT, VPos.TOP)
      }
    }
  }


  private fun handleMouseEvent(event: MouseEvent) {
    val target = event.target

    when (event.eventType) {
      MouseEvent.MOUSE_PRESSED -> {
        if (target is Handle<*>) {
          sharedLock.tryLock(target) {
            event.consume()

            val node = target.node
            val minWidth = node.minWidth(height)
            val width = node.prefWidth(height)
            val maxWidth = node.maxWidth(height)
            val r = Resize(event.scenePosition, target.deltaWidth, minWidth, width, maxWidth)
            r
          }
        }
      }

      MouseEvent.DRAG_DETECTED -> {
        startFullDrag()
      }

      MouseEvent.MOUSE_DRAGGED -> {
        sharedLock.ifLocked(Handle::class.java, Resize::class.java) { lock ->
          event.consume()

          val node = lock.owner.node
          val resize = lock.value
          // TODO unnötiger Aufwand?
          val deltaX = node.screenDeltaToLocal(Point2D(event.scenePosition.x - resize.scenePosition.x, 0.0)).x

          lock.owner.resize(resize.deltaWidth + deltaX)

//          var deltaWidth = resize.deltaWidth + deltaX
//          val newWidth = resize.current + deltaWidth
//          if (newWidth < resize.min) {
//            deltaWidth = resize.min - resize.current
//          } else {
//            if (newWidth > resize.max) {
//              deltaWidth = resize.max - resize.current
//            }
//          }
//          lock.owner.deltaWidth = deltaWidth
          nodeLayer.requestLayout()
          handleLayer.requestLayout()
        }
      }

      MouseEvent.MOUSE_RELEASED -> {
        sharedLock.ifLocked(Handle::class.java, Resize::class.java) { lock ->
          event.consume()
          nodeLayer.requestLayout()
          handleLayer.requestLayout()
          lock.releaseLock()

          if (event.clickCount > 1) {
            onDoubleClick(lock.owner.node as T)
          }
        }
      }
    }
  }

  private data class Resize(
    val scenePosition: Point2D,
    val deltaWidth: Double,
    val min: Double,
    val current: Double,
    val max: Double
  )

  private class NodesLayer<T : Node>(
    internal val nodes: ReadOnlyObjectProperty<List<T>>,
    internal val handles: ObservableList<Handle<T>>,
  ) : Region() {
    init {
      isFocusTraversable = false

      children.syncWith(nodes) { it }
      children.addListener(ListChangeListener {
        requestLayout()
      })
    }

    override fun computeMinHeight(width: Double): Double {
      val height = children.map { it.minHeight(width) }.fold(0.0) { l, r -> max(l, r) }
      return insets.top + height + insets.bottom
    }

    override fun computeMinWidth(height: Double): Double {
      val width = children.map { it.minWidth(height) }.fold(0.0) { l, r -> l + r }
      return insets.left + width + insets.right
    }

    override fun computePrefHeight(width: Double): Double {
      val height = children.map { it.prefHeight(width) }.fold(0.0) { l, r -> max(l, r) }
      return insets.top + height + insets.bottom
    }

    override fun computePrefWidth(height: Double): Double {
      val deltaWidthMap = handles.map { it.node to it.deltaWidth }.toMap()
      val width = children.map { it.prefWidth(height) + deltaWidthMap[it]!! }.fold(0.0) { l, r -> l + r }
      return insets.left + width + insets.right
    }

    override fun layoutChildren() {
      layoutChildren(layoutX, layoutY, width, height)
    }

    private fun layoutChildren(_contentX: Double, _contentY: Double, _contentWidth: Double, _contentHeight: Double) {
      val deltaWidthMap = handles.map { it.node to it.deltaWidth }.toMap()

//      println("layout: $deltaWidthMap")

      val top = insets.top
      val right = insets.right
      val left = insets.left
      val bottom = insets.bottom

      val contentHeight = _contentHeight - top - bottom

      var x = left
      children.forEach { node ->
        val width = node.prefWidth(height) + deltaWidthMap[node]!!
        val fixedWidth = snappedToPixel(width)
        layoutInArea(node, x, 0.0, fixedWidth, contentHeight, -1.0, HPos.LEFT, VPos.TOP)
        x = x + fixedWidth
      }

    }
  }

  class HandleLayer<T : Node>(
    internal val handles: ObservableList<Handle<T>>,
  ) : Region() {

    init {
      isPickOnBounds = false
      isFocusTraversable = false

      children.syncWith(handles) { it }
//      isMouseTransparent = true
      children.addListener(ListChangeListener {
        requestLayout()
      })
    }

    fun setSize(node: T, min: Double, preferred: Double) {
      val handle = requireNotNull(handles.find { it.node == node }) { "could not find handle for $node" }
      handle.setSize(min, preferred)
    }

    override fun layoutChildren() {
      layoutChildren(layoutX, layoutY, width, height)
    }

    private fun layoutChildren(_contentX: Double, _contentY: Double, _contentWidth: Double, _contentHeight: Double) {
      val top = insets.top
      val right = insets.right
      val left = insets.left
      val bottom = insets.bottom

      val contentHeight = _contentHeight - top - bottom

      children.forEach {
        val handle = (it as Handle<T>)
        val node = handle.node
        val bounds = node.boundsInParent
        val width = handle.width
        layoutInArea(handle, bounds.maxX - width, bounds.minY, width, contentHeight, -1.0, HPos.LEFT, VPos.TOP)
      }
    }
  }

  class Handle<T : Node>(val node: T) : Pane() {
    var deltaWidth: Double = 0.0
    var min: Double? = null

    fun resize(newDeltaWidth: Double) {
      val minWidth = node.minWidth(height)
      val width = node.prefWidth(height)
      val maxWidth = node.maxWidth(height)

      val preferred = width + newDeltaWidth

      deltaWidth = if (preferred > maxWidth) {
        maxWidth - width
      } else if (preferred < max(minWidth, min ?: minWidth)) {
        max(minWidth, min ?: minWidth) - width
      } else {
        preferred - width
      }
    }

    fun setSize(min: Double, preferred: Double) {
//      println("$node setSize: $min, $preferred")
      this.min = min

      node.applyCss()
      val minWidth = node.minWidth(height)
      val width = node.prefWidth(height)
      val maxWidth = node.maxWidth(height)

      deltaWidth = if (preferred > maxWidth) {
        maxWidth - width
      } else if (preferred < minWidth) {
        minWidth - width
      } else {
        if (preferred > width)
            preferred - width
        else
          0.0
      }
    }


    init {
      styleClass.addAll("split-pane-handle")
//      minWidth = 10.0
//      maxWidth = 10.0
    }

  }
}