package de.flapdoodle.kfx.controls.grapheditor.commands

import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import javafx.geometry.Point2D

sealed class Command {
  class Abort() : Command()
  class AskForPosition(
    val onSuccess: (Point2D) -> Unit
  ) : Command()
  class PanTo(
    val position: Point2D,
    val onSuccess: () -> Unit
  ) : Command()
}