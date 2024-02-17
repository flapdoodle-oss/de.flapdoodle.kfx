package de.flapdoodle.kfx.extensions

import javafx.beans.value.ObservableValue
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.util.Subscription

object Nodes {

  fun pick(
    container: Node,
    center: Point2D,
    distance: Double,
    toLocal: (Node, Point2D) -> Point2D
  ): Sequence<Node> {
    return sequence {
      pick(container, square(center, distance), toLocal)
    }
  }

  private suspend fun SequenceScope<Node>.pick(
    container: Node,
    points: Array<Point2D>,
    toLocal: (Node, Point2D) -> Point2D
  ) {
    if (contains(container, points, toLocal)) {
      if (container is Parent) {
        val children = container.childrenUnmodifiable.reversed()
        children.forEach {
          pick(it, points, toLocal)
        }
      }
      yield(container)
    }
  }

  private fun contains(
    node: Node,
    points: Array<Point2D>,
    toLocal: (Node, Point2D) -> Point2D
  ): Boolean {
    if (!node.isPickOnBounds) {
      if (node is Parent) {
        return node.childrenUnmodifiable.reversed()
          .any { contains(it, points, toLocal) }
      }
    }
    return points.any { node.contains(toLocal(node, it)) }
  }

  private fun square(center: Point2D, distance: Double): Array<Point2D> {
    require(distance >= 0.0) { "distance < 0.0" }
    val half = distance / 2.0

    return if (distance == 0.0)
      arrayOf(center)
    else
      arrayOf(
        center.add(-half, -half),
        center.add(half, -half),
        center.add(half, half),
        center.add(-half, half)
      )
  }

  fun onAttach(node: Node, onAttach: () -> Unit): WithOnAttach {
    return WithOnAttach(node, onAttach)
  }

  fun onAttachDetach(node: Node, onAttach: () -> Unit, onDetach: () -> Unit) {
    val listener: (observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) -> Unit = { observable, oldValue, newValue ->
      if (oldValue==null && newValue!=null) {
        onAttach()
      }
      if (oldValue!=null && newValue == null) {
        onDetach()
      }
    }
    node.sceneProperty().addListener(listener)
  }

  fun onAttach(node: Node, action: () -> Subscription) {
    val listener: (observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) -> Unit = { observable, oldValue, newValue ->
      if (oldValue==null && newValue!=null) {
        val old = node.property[Subscription::class]
        val new = action()
        node.property[Subscription::class] = if (old!=null) old.and(new) else new
      }
      if (oldValue!=null && newValue == null) {
        node.property[Subscription::class]?.unsubscribe()
        node.property[Subscription::class] = null
      }
    }
    node.sceneProperty().addListener(listener)
  }

  class WithOnAttach(val node: Node, val onAttach: () -> Unit) {
    fun onDetach(onDetach: () -> Unit) {
      Nodes.onAttachDetach(node, onAttach, onDetach)
    }
  }
}