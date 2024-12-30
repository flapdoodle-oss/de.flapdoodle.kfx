package de.flapdoodle.kfx.tasks

import javafx.concurrent.Task

object TaskFactory {
  fun <T> factory(taskFactory: () -> Task<T>): () -> Task<T> {
    return taskFactory
  }
}