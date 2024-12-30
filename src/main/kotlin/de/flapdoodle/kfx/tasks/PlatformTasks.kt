package de.flapdoodle.kfx.tasks

import javafx.application.Platform
import javafx.concurrent.Task

object PlatformTasks {
  fun runLater(runnable: Runnable): Task<Void> {
    return object: Task<Void>() {
      override fun call(): Void? {
        Platform.runLater(runnable)
        return null
      }
    }
  }
}