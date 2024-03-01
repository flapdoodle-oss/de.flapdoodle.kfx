package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.commands

import de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.types.VertexId
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