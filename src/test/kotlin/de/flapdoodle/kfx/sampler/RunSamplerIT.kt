package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.App
import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class RunSamplerIT {

    @Start
    private fun createElement(stage: Stage) {
        val testee = PanningWindowsSampler().apply {
            start(stage)
        }
    }

    @Test
    @Disabled
    fun waitSomeTime(robot: FxRobot) {
        println("running for one minute...")
        Thread.sleep(60*1000)
    }
}