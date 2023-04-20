package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.extensions.ObservableLists
import de.flapdoodle.kfx.nodeeditor.model.Slot
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class ConnectorsPane(
  private val slots: ObservableList<Slot>,
  val mode: Slot.Mode
) : Pane() {

  init {
    border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths.DEFAULT))
    val filtered = slots.filtered { it.mode == mode }
    val vBox = VBox()
    ObservableLists.syncWith(filtered, vBox.children) { c -> Connector(c) }
    children.add(vBox)
  }
}