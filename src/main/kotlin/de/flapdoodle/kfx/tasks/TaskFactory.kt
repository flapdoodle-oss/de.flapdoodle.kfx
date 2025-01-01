package de.flapdoodle.kfx.tasks

import javafx.concurrent.Task

fun interface TaskFactory<T> {
  fun create(): Task<T>
}
