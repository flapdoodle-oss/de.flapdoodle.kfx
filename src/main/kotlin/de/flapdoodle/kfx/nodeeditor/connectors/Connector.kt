package de.flapdoodle.kfx.nodeeditor.connectors

import de.flapdoodle.kfx.nodeeditor.model.Slot
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class Connector(val slot: Slot) : HBox() {
  init {
    when (slot.mode) {
      Slot.Mode.IN -> children.addAll(Label("->"), Label(slot.name))
      Slot.Mode.OUT -> children.addAll(Label(slot.name), Label("->"))
    }

    if (false) {
      localToSceneTransformProperty().addListener(ChangeListener { observable, oldValue, newValue ->
        println("-> ${localToScene(Point2D(0.0, 0.0))}")
      })
    }
  }
}