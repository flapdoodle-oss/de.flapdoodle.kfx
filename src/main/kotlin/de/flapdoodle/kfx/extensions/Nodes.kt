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

//  fun onAttach(node: Node, onAttach: () -> Subscription) {
//    var subscription: Subscription? = null
//
//    val listener: (observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) -> Unit = { _, oldValue, newValue ->
//      if (oldValue==null && newValue!=null) {
//        subscription = onAttach()
//      }
//      if (oldValue!=null && newValue == null) {
//        subscription?.unsubscribe()
//      }
//    }
//    node.sceneProperty().addListener(listener)
//  }

  fun <T> onAttach(node: Node, onAttach: () -> T): WithOnAttach<T> {
    return WithOnAttach(node, onAttach)
  }

  fun <T> onAttachDetach(node: Node, onAttach: () -> T, onDetach: (T?) -> Unit) {
    var result: T? = null
    val listener: (observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) -> Unit = { _, oldValue, newValue ->
      if (oldValue==null && newValue!=null) {
        result = onAttach()
      }
      if (oldValue!=null && newValue == null) {
        onDetach(result)
        result=null
      }
    }
    node.sceneProperty().addListener(listener)
  }

  fun unsubscribeOnDetach(node: Node, action: () -> Subscription) {
    onAttachDetach(node ,action) { it?.unsubscribe() }
  }

  class WithOnAttach<T>(val node: Node, val onAttach: () -> T) {
    fun onDetach(onDetach: (T?) -> Unit) {
      onAttachDetach(node, onAttach, onDetach)
    }
  }

  fun <T> onBindToParent(node: Node, bind: () -> T, unbind: (T) -> Unit): Subscription {
    return node.parentProperty().subscribe(bindUnbindChangeListener(bind, unbind))
  }

  fun <T> onBindToScene(node: Node, bind: () -> T, unbind: (T) -> Unit): Subscription {
    return node.sceneProperty().subscribe(bindUnbindChangeListener(bind, unbind))
  }

  private fun <P, T> bindUnbindChangeListener(bind: () -> T, unbind: (T) -> Unit): (P?, P?) -> Unit {
    var result: T? = null
    return { old, new ->
      if (old==null && new!=null) {
        result = bind()
      }
      if (old!=null && new == null) {
        val lastResult = result
        if (lastResult!=null) {
          unbind(lastResult)
        }
        result=null
      }
    }
  }
}