package de.flapdoodle.kfx.controls.grapheditor.events

import de.flapdoodle.kfx.controls.grapheditor.GraphEditor

@FunctionalInterface
fun interface EventListener {
  fun onEvent(graphEditor: GraphEditor, event: Event): Boolean
}