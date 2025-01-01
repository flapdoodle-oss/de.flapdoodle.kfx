package de.flapdoodle.kfx.tasks

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

object Timelines {

  fun executeAll(vararg tasks: Runnable): Builder {
    return Builder(listOf(
      *tasks
    ))
  }

  data class Builder(
    val runnables: List<Runnable>,
    private val repeating: Boolean = false,
    private val delay: Duration = Duration.ZERO,
    private val period: Duration = Duration.ZERO
  ) {

    fun waitFor(delay: Duration): Builder {
      return copy(delay = delay)
    }

    fun nextAfter(period: Duration): Builder {
      return copy(period = period)
    }

    fun repeating(): Builder {
      return copy(repeating = true)
    }

    fun asTimeline(): Timeline {
      val keyframes = runnables.mapIndexed { index, runnable ->
        KeyFrame(period.multiply((index+1).toDouble()), {
          runnable.run()
        })
      }
      val timeline = Timeline()
      timeline.keyFrames.setAll(keyframes)
      timeline.cycleCount = if (repeating) Animation.INDEFINITE else 1
      timeline.delay = delay

      return timeline
    }
  }
}