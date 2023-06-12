package de.flapdoodle.kfx.sampler

import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class PanningWindowsSamplerTest {
  @Start
  private fun createElement(stage: Stage) {
    PanningWindowsSampler().apply {
      start(stage)
    }
  }

  @Test
  @Disabled
  fun waitSomeTime(robot: FxRobot) {
    println("running for one minute...")
    Thread.sleep(2 * 60 * 1000)
  }

}