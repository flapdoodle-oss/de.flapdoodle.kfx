package de.flapdoodle.kfx.controls.graphmodeleditor.model

interface VertexContentFactory<T> {
  fun vertexContent(value: T): VertexContent<T>
}