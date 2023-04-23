package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent

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
}