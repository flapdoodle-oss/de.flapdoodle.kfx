package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.ValueOfValueBinding
import de.flapdoodle.kfx.bindings.Values
import de.flapdoodle.kfx.bindings.defaultIfNull
import de.flapdoodle.kfx.bindings.mapToDouble
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.shape.Line
import java.util.*

class NodeConnection(val name: String, val start: UUID, val end: UUID): Region() {
  companion object {
    fun onlyConnections(node: javafx.scene.Node): List<NodeConnection> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<NodeConnection>()
      } else {
        emptyList<NodeConnection>()
      }
    }
  }

  private val startNode = SimpleObjectProperty<Node?>()
  private val endNode = SimpleObjectProperty<Node?>()
  private val line = Line(0.0, 0.0, 100.0, 50.0);

  init {
    children.add(line.apply {
      strokeWidth = 1.0
    })

    val startPoint = ValueOfValueBinding.of(startNode, Node::someFakeHandleCoord)
      .defaultIfNull(Values.constantObject(Point2D(0.0, 0.0)))
    val endPoint = ValueOfValueBinding.of(endNode, Node::someFakeHandleCoord)
      .defaultIfNull(Values.constantObject(Point2D(0.0, 0.0)))

    line.startXProperty().bind(startPoint.mapToDouble(Point2D::getX))
    line.startYProperty().bind(startPoint.mapToDouble(Point2D::getY))

    line.endXProperty().bind(endPoint.mapToDouble(Point2D::getX))
    line.endYProperty().bind(endPoint.mapToDouble(Point2D::getY))
  }

  fun init(resolver: (UUID) -> ObjectBinding<Node?>) {
    startNode.bind(resolver(start))
    endNode.bind(resolver(end))
  }

  fun lineBoundsInParent(): Bounds {
    return line.boundsInParent
  }
}