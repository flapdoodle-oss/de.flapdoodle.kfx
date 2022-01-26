package de.flapdoodle.kfx.layout.grid

import javafx.scene.Scene
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class WeightGridPaneIT {

    @Start
    private fun createElement(stage: Stage) {
        stage.scene = Scene(WeightGridPane(),200.0,200.0)
        stage.show()
    }

    @Test
    fun justShow(robot: FxRobot) {
        assertThat(robot.lookup(".root").queryAs(WeightGridPane::class.java))
            .satisfies(ThrowingConsumer {
                assertThat(it.verticalSpace.value).isEqualTo(0.0)
                assertThat(it.horizontalSpace.value).isEqualTo(0.0)
            })

    }
}