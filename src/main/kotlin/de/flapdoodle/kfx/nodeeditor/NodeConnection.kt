package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.types.AngleAtPoint2D
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.css.PseudoClass
import javafx.geometry.Bounds
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.paint.Color

class NodeConnection(
  val name: String,
  val start: NodeSlotId,
  val end: NodeSlotId
): Region() {
  companion object {
    fun onlyConnections(node: javafx.scene.Node): List<NodeConnection> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<NodeConnection>()
      } else {
        emptyList()
      }
    }
  }

  object Style {
    val Focused = PseudoClassWrapper<NodeConnection>(PseudoClass.getPseudoClass("focused"))
    val Selected = PseudoClassWrapper<NodeConnection>(PseudoClass.getPseudoClass("selected"))
  }


  val registry = SimpleObjectProperty<NodeRegistry>()
  
  private val startNode = SimpleObjectProperty<Node?>()
  private val endNode = SimpleObjectProperty<Node?>()
//  private val line = Line(0.0, 0.0, 100.0, 50.0)

//  private val startConnector = ValueOfValueBinding.of(startNode, Node::someFakeConnector)
//    .defaultIfNull(Values.constantObject(AngleAtPoint2D(Point2D(0.0, 0.0), 0.0)))
//  private val endConnector = ValueOfValueBinding.of(endNode, Node::someFakeConnector)
//    .defaultIfNull(Values.constantObject(AngleAtPoint2D(Point2D(0.0, 0.0), 0.0)))
  private val startConnector = SimpleObjectProperty(AngleAtPoint2D(0.0, 0.0, 0.0))
  private val endConnector = SimpleObjectProperty(AngleAtPoint2D(0.0, 0.0, 0.0))

  private val curve = Curves.cubicCurve(startConnector, endConnector)

  init {
    styleClass.addAll("nodeConnection")
    stylesheets += javaClass.getResource("NodeConnection.css").toExternalForm()
    //isFocusTraversable = false

    children.add(curve.apply {
      //Markers.markAsConnection(this, ConnectionId(start, end))
      styleClass.addAll("path")
      
//      strokeWidth = 1.0
//      stroke = Color.RED
      fill = Color.TRANSPARENT
      isPickOnBounds = false
    })
    isPickOnBounds = false
  }

  fun init(resolver: (NodeSlotId) -> ObjectBinding<AngleAtPoint2D>) {
    startConnector.bind(resolver(start).map { AngleAtPoint2D(sceneToLocal(it.point2D), it.angle) })
    endConnector.bind(resolver(end).map { AngleAtPoint2D(sceneToLocal(it.point2D), it.angle) })
  }

  fun dispose() {
    startNode.unbind()
    endNode.unbind()
  }

  fun boundsInParent(): Bounds {
    return curve.boundsInParent
  }

}