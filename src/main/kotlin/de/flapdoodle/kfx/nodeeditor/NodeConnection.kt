package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.graph.nodes.Curves
import de.flapdoodle.kfx.nodeeditor.types.NodeSlotId
import de.flapdoodle.kfx.strokes.LinearGradients
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.css.PseudoClass
import javafx.geometry.Bounds
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop

class NodeConnection(
  val name: String,
  val start: NodeSlotId,
  val end: NodeSlotId,
  private var selected: Boolean = false
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

  private object Style {
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
  private val startConnector = SimpleObjectProperty(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK))
  private val endConnector = SimpleObjectProperty(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK))

  private val curve = Curves.cubicCurve(startConnector, endConnector)

  init {
    styleClass.addAll("nodeConnection")
    stylesheets += javaClass.getResource("NodeConnection.css").toExternalForm()
    //isFocusTraversable = false

//    val linearGrad = LinearGradient(
//      0.0,  // start X
//      0.0,  // start Y
//      1.0,  // end X
//      1.0,  // end Y
//      true,  // proportional
//      CycleMethod.NO_CYCLE,  // cycle colors
//      // stops
//      Stop(0.1, Color.rgb(255, 0, 0, .991)),
//      Stop(1.0, Color.rgb(0, 255, 0, .991))
//    )

    children.add(curve.apply {
      styleClass.addAll("path")
      
//      stroke = linearGrad
      strokeProperty().bind(LinearGradients.exact(
        startConnector.map { it.point2D },
        endConnector.map { it.point2D },
        startConnector.map { it.color },
        endConnector.map { it.color }
      ))
      fill = Color.TRANSPARENT
      isPickOnBounds = false
    })
    isPickOnBounds = false
  }

  fun init(resolver: (NodeSlotId) -> ObjectBinding<ColoredAngleAtPoint2D>) {
    startConnector.bind(resolver(start).map { ColoredAngleAtPoint2D(sceneToLocal(it.point2D), it.angle, it.color) })
    endConnector.bind(resolver(end).map { ColoredAngleAtPoint2D(sceneToLocal(it.point2D), it.angle, it.color) })
  }

  fun dispose() {
    startNode.unbind()
    endNode.unbind()
  }

  fun boundsInParent(): Bounds {
    return curve.boundsInParent
  }

  fun isSelected(): Boolean {
    return selected
  }

  fun toggleSelect() {
    Style.Selected.swap(this)
    selected = !selected
  }

  fun focus() {
    Style.Focused.enable(this)
  }

  fun blur() {
    Style.Focused.disable(this)
  }

}