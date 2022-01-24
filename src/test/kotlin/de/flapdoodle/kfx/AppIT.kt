package de.flapdoodle.kfx

import javafx.scene.Scene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class AppIT {

    @Start
    private fun createElement(stage: Stage) {
        val app = App().apply { start(stage) }
    }

    @Test
    fun primarySceneButton(robot: FxRobot) {
        val button = robot.lookup("#primaryButton").queryButton()
        assertThat(button).hasText("Switch to Secondary View")
    }

    @Test
    fun clickOnPrimarySceneButtonSwitchToSecondary(robot: FxRobot) {
        robot.clickOn("#primaryButton")

        assertThat(robot.lookup("#secondaryButton").queryButton()).hasText("Switch to Primary View")
    }
}