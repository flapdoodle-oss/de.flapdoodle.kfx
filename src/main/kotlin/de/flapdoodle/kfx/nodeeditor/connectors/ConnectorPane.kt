package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.extensions.ObservableLists
import de.flapdoodle.kfx.nodeeditor.Connector
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class ConnectorPane(
  private val connectors: ObservableList<Connector>,
  val mode: Connector.Mode
) : Pane() {

  init {
    border = Border(BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii(1.0), BorderWidths.DEFAULT))
    val filtered = connectors.filtered { it.mode == mode }
    val vBox = VBox()
    ObservableLists.syncWith(filtered, vBox.children) { c -> Label(c.name) }
    children.add(vBox)
  }
}