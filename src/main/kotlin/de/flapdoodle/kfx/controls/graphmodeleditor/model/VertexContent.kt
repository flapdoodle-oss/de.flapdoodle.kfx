package de.flapdoodle.kfx.controls.graphmodeleditor.model

import javafx.beans.property.Property
import javafx.scene.Node

data class VertexContent<T>(
  val node: Node,
  val valueModel: Property<T>
)
