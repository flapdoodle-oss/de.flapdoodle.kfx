package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.nodeeditor.NodeConnection
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.shape.CubicCurve

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
    if (node is CubicCurve) {
      println("got curve...")
      points.any {
        val point = toLocal(node,it)
        val hit = node.intersects(point.x,point.y, 1.0, 1.0)
        println("curve intersect $point -> $hit")
        hit
      }
    }
    
    if (!node.isPickOnBounds) {
//      println("node -> $node")
      if (node is NodeConnection && false) {
//        val hit = points.any { node.contains(toLocal(node, it)) }
//        println("$node -> $hit ($points)")
//        val bound = BoundingBoxes.around(points.map { toLocal(node, it) })
//        val intersect = node.intersects(bound)
//        println("$node -> $intersect ($bound)")
        points.any {
          val point = toLocal(node,it)
          val hit = node.intersects(point.x,point.y, 1.0, 1.0)
          println("intersect $point -> $hit")
          hit
        }
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