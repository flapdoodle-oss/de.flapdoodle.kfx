package de.flapdoodle.kfx.nodeeditor

import de.flapdoodle.kfx.bindings.*
import javafx.beans.InvalidationListener
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import javafx.scene.layout.Region
import javafx.scene.shape.Line
import java.util.*

class NodeConnection(val name: String, val start: UUID, val end: UUID): Region() {
  private val startNode = SimpleObjectProperty<Node?>()
  private val endNode = SimpleObjectProperty<Node?>()
  private val line = Line(0.0, 0.0, 100.0, 50.0);

  init {
    children.add(line.apply {
      strokeWidth = 1.0
    })

    // TODO mmosmann - bekommt ja nix davon mit, dass sich layoutX geändert hat

    line.startXProperty()
      .bind(ValueOfValueBinding.of(startNode, Node::someFakeHandleCoord).mapToDouble { it?.x ?: 0.0 })

    line.startYProperty()
      .bind(ValueOfValueBinding.of(startNode, Node::someFakeHandleCoord).mapToDouble { it?.y ?: 0.0 })

    line.endXProperty().bind(
      ValueOfValueBinding.of(endNode, javafx.scene.Node::layoutXProperty)
        .defaultIfNull(Values.constant(0.0))
    )
    line.endYProperty().bind(
      ValueOfValueBinding.of(endNode, javafx.scene.Node::layoutYProperty)
        .defaultIfNull(Values.constant(0.0))
    )
    
    startNode.addListener(InvalidationListener {
      println("start changed...")
    })
    endNode.addListener(InvalidationListener {
      println("end changed...")
    })
    line.startXProperty().addListener(ChangeListener { observable, oldValue, newValue ->
      println("start x changed to $newValue")
    })
  }

  fun init(resolver: (UUID) -> ObjectBinding<Node?>) {
    startNode.bind(resolver(start))
    endNode.bind(resolver(end))
  }
}