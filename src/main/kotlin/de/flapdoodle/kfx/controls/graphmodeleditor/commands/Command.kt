package de.flapdoodle.kfx.controls.graphmodeleditor.commands

import de.flapdoodle.kfx.controls.graphmodeleditor.types.VertexId
import javafx.geometry.Point2D

sealed class Command<V> {
  class Abort<V>() : Command<V>()
  class AskForPosition<V>(
    val onSuccess: (Point2D) -> Unit
  ) : Command<V>()
  class FindVertex<V>(
    val vertex: VertexId<V>,
    val onSuccess: () -> Unit
  ) : Command<V>()
}