package de.flapdoodle.kfx.usecase.tab2.graphmodeleditor.model

interface VertexContentFactory<T> {
  fun vertexContent(value: T): VertexContent<T>
}