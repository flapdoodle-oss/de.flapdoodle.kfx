package de.flapdoodle.kfx.extensions

import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent

object Nodes {

  fun hit(
    container: Node,
    center: Point2D,
    distance: Double,
    toLocal: (Node, Point2D) -> Point2D = Node::screenToLocal
  ): Sequence<Node> {
    return sequence {
      hit(container, square(center, distance), toLocal)
    }
  }

  private suspend fun SequenceScope<Node>.hit(
    container: Node,
    points: Array<Point2D>,
    toLocal: (Node, Point2D) -> Point2D = Node::screenToLocal
  ) {
    if (contains(container, points, toLocal)) {
      yield(container)
      if (container is Parent) {
        val children = container.childrenUnmodifiable.reversed()
        children.forEach {
          hit(it, points, toLocal)
        }
      }
    }
  }

  private fun contains(
    node: Node,
    points: Array<Point2D>,
    toLocal: (Node, Point2D) -> Point2D
  ): Boolean {
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

  fun pick(node: Node, sceneX: Double, sceneY: Double): Node? {
    var p: Point2D? = node.sceneToLocal(sceneX, sceneY, true /* rootScene */)

    // check if the given node has the point inside it, or else we drop out
    if (!node.contains(p)) return null

    // at this point we know that _at least_ the given node is a valid
    // answer to the given point, so we will return that if we don't find
    // a better child option
    if (node is Parent) {
      // we iterate through all children in reverse order, and stop when we find a match.
      // We do this as we know the elements at the end of the list have a higher
      // z-order, and are therefore the better match, compared to children that
      // might also intersect (but that would be underneath the element).
      var bestMatchingChild: Node? = null
      val children: List<Node> = node.childrenUnmodifiable
      for (i in children.indices.reversed()) {
        val child = children[i]
        p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */)
        if (child.isVisible && !child.isMouseTransparent && child.contains(p)) {
          bestMatchingChild = child
          break
        }
      }
      if (bestMatchingChild != null) {
        return pick(bestMatchingChild, sceneX, sceneY)
      }
    }
    return node
  }

}