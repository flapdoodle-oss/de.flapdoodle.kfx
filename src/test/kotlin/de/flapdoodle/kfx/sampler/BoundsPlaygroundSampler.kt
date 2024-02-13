package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.demo.BoundsPlayground
import javafx.application.Application

class BoundsPlaygroundSampler {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(BoundsPlayground::class.java, *args)
    }
  }

}