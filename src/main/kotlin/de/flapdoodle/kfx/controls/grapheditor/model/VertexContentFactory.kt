package de.flapdoodle.kfx.controls.grapheditor.model

interface VertexContentFactory<T> {
  fun vertexContent(value: T): VertexContent<T>
}