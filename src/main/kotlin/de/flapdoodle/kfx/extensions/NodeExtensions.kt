/**
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

import de.flapdoodle.kfx.types.AngleAndPoint2D
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

fun Node.hide() {
  isVisible = false
  isManaged = false
}

fun Node.show() {
  isVisible = true
  isManaged = true
}

val Node.property: ObservableMapExtensions.TypedMap
  get() = ObservableMapExtensions.TypedMap(this.properties)

val Node.constraint: ObservableMapExtensions.TypedMap
  get() = object : ObservableMapExtensions.TypedMap(this.properties) {
    override fun <T : Any> set(key: Key<T>, value: T?): T? {
      return super.set(key, value).also {
        this@constraint.parent?.requestLayout()
      }
    }

    override fun <T : Any> set(type: KClass<T>, value: T?): T? {
      return super.set(type, value).also {
        this@constraint.parent?.requestLayout()
      }
    }
  }

fun Node.pickScreen(center: Point2D, distance: Double = 0.0): Sequence<Node> {
  return Nodes.pick(this, center, distance, Node::screenToLocal)
}

fun Node.pickScene(center: Point2D, distance: Double = 0.0): Sequence<Node> {
  return Nodes.pick(this, center, distance, Node::sceneToLocal)
}

fun <T : Any> Parent.findAllInTree(type: KClass<T>): List<T> {
  return childrenUnmodifiable.flatMap {
    val nodeAsList = if (type.isInstance(it)) {
      @Suppress("UNCHECKED_CAST")
      listOf(it as T)
    } else
      emptyList()

    val sub = if (it is Parent) it.findAllInTree(type) else emptyList()

    nodeAsList + sub
  }
}

fun Node.parentPath(child: Node): List<Node> {
  return if (child.parent == this) {
    listOf(this, child)
  } else {
    val parentOfChild = child.parent
    return this.parentPath(parentOfChild) + child
  }
}


fun <T : Node> Node.parentOfType(type: KClass<T>): T? {
  println("parent of $this -> $parent (search $type)")
  if (parent == null) return null
  type.safeCast(parent)?.also { return it }
  return parent?.parentOfType(type)
}

fun Node.widthLimits(): Pair<Double, Double> {
  val minW = this.minWidth(-1.0)
  return if (isResizable) {
    val maxW = maxWidth(-1.0)
    Pair(minW, if (maxW>0.0) maxW else Double.MAX_VALUE)
  } else {
    Pair(minW, minW)
  }
}

fun Node.heightLimits(): Pair<Double, Double> {
  val minH = minHeight(-1.0)
  return if (isResizable) {
    val maxH = maxHeight(-1.0)
    Pair(minH, if (maxH>0.0) maxH else Double.MAX_VALUE)
  } else {
    Pair(minH, minH)
  }
}

fun Node.screenDeltaToLocal(delta: Point2D): Point2D {
  return screenToLocal(delta) - screenToLocal(Point2D.ZERO)
}

var Node.layoutPosition: Point2D
  get() = Point2D(layoutX, layoutY)
  set(value: Point2D) {
    relocate(value.x, value.y)
//    layoutX = value.x
//    layoutY = value.y
  }

fun Node.screenToLocal(src: AngleAtPoint2D?): AngleAtPoint2D? {
  return if (src != null) {
    val result = screenToLocal(src.point2D)
    if (result!=null) AngleAtPoint2D(result, src.angle) else null
  } else null
}

fun Node.sceneToLocal(src: AngleAtPoint2D?): AngleAtPoint2D? {
  return if (src != null) {
    val result = sceneToLocal(src.point2D)
    if (result!=null) src.copy(point2D = result) else null
  } else null
}

fun Node.sceneToLocal(src: ColoredAngleAtPoint2D?): ColoredAngleAtPoint2D? {
  return if (src != null) {
    val result = sceneToLocal(src.point2D)
    if (result!=null) src.copy(point2D = result) else null
  } else null
}