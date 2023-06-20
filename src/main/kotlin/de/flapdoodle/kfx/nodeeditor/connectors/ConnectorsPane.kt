package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.nodeeditor.NodeRegistry
import de.flapdoodle.kfx.nodeeditor.model.Position
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.layout.*

class ConnectorsPane(
  private val registry: ObservableValue<NodeRegistry>,
  private val nodeId: NodeId,
  slots: ObservableList<Slot>,
  private val position: Position
) : Pane() {

  init {
//    border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths.DEFAULT))

    val filtered = slots.filtered { it.position == position }

    val wrapper = when (position) {
      Position.LEFT -> VBox().apply { spacing = 2.0 }
      Position.RIGHT -> VBox().apply { spacing = 2.0 }
      Position.BOTTOM -> HBox().apply { spacing = 2.0 }
    }

    ObservableLists.syncWith(filtered, wrapper.children) { c -> Connector(registry, nodeId, c, position) }
    children.add(wrapper)
  }


}