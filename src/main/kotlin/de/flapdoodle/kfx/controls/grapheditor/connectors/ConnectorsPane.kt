package de.flapdoodle.kfx.controls.grapheditor.connectors

import de.flapdoodle.kfx.bindings.ObservableLists
import de.flapdoodle.kfx.controls.grapheditor.Registry
import de.flapdoodle.kfx.controls.grapheditor.model.Position
import de.flapdoodle.kfx.controls.grapheditor.model.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.util.Subscription

class ConnectorsPane(
  private val registry: ObservableValue<Registry>,
  private val vertexId: VertexId,
  slots: ObservableList<Slot>,
  private val position: Position
) : Pane() {

  private val syncJob: Subscription

  init {
//    border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths.DEFAULT))

    val filtered = slots.filtered { it.position == position }

    val wrapper = when (position) {
      Position.LEFT -> VBox().apply { spacing = 2.0 }
      Position.RIGHT -> VBox().apply { spacing = 2.0 }
      Position.BOTTOM -> HBox().apply { spacing = 2.0 }
    }

    syncJob = ObservableLists.syncWith(filtered, wrapper.children) { c ->
      Connector(registry, vertexId, c, position)
    }
    children.add(wrapper)
  }


}