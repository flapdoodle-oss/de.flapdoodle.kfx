package de.flapdoodle.kfx.controls.graphmodeleditor.model

data class Change<T>(
  val removed: Set<T>,
  val notChanged: Set<T>,
  val modified: Set<Pair<T,T>>,
  val added: Set<T>
)