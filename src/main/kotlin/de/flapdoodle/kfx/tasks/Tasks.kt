package de.flapdoodle.kfx.tasks

import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.EventHandler
import javafx.util.Duration

object Tasks {

  fun <T : Any> executeAll(vararg tasks: TaskFactory<T>): Builder<T> {
    return Builder(listOf(*tasks))
  }

  data class Builder<T : Any>(
    val taskFactories: List<TaskFactory<T>>,
    private val repeating: Boolean = false,
    private val delay: Duration = Duration.ZERO,
    private val period: Duration = Duration.ZERO
  ) {

    fun waitFor(delay: Duration): Builder<T> {
      return copy(delay = delay)
    }

    fun nextAfter(period: Duration): Builder<T> {
      return copy(period = period)
    }

    fun repeating(): Builder<T> {
      return copy(repeating = true)
    }

    fun asScheduledService(): ScheduledService<T> {
      val service = object : ScheduledService<T>() {
        private var index = 0

        override fun createTask(): Task<T> {
          var taskFactory = taskFactories[index]
          index++

          if (index == taskFactories.size) {
            if (repeating) {
              index = 0
            } else {
              cancel()
            }
          }

          return taskFactory.create()
        }
      }
      service.delay = delay
      service.period = period
      service.onFailed = EventHandler<WorkerStateEvent> {
        println("failed: $it")
      }
      return service
    }
  }
}