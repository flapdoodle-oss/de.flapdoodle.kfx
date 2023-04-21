package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.extensions.ObservableLists
import de.flapdoodle.kfx.nodeeditor.NodeRegistry
import de.flapdoodle.kfx.nodeeditor.model.Slot
import de.flapdoodle.kfx.nodeeditor.types.NodeId
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.layout.*
import javafx.scene.paint.Color

class ConnectorsPane(
  private val registry: ObservableValue<NodeRegistry>,
  private val nodeId: NodeId,
  private val slots: ObservableList<Slot>,
  val mode: Slot.Mode
) : Pane() {

  init {
    border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths.DEFAULT))
    val filtered = slots.filtered { it.mode == mode }
    val vBox = VBox()
//    vBox.children.addListener(AddOrRemoveListChangeListener(
//      onAdded = {
//        println("added ${it}")
//      },
//      onRemoved = {
//        println("removed $it")
//      }
//    ))
    ObservableLists.syncWith(filtered, vBox.children) { c -> Connector(registry, nodeId, c) }
    children.add(vBox)
  }

  
}