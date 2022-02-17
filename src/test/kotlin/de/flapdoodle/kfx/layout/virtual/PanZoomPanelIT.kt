package de.flapdoodle.kfx.layout.virtual

import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class PanZoomPanelIT {

    @Start
    private fun createElement(stage: Stage) {
        val panningWindow = PanZoomPanel()
        val content = Rectangle()
        content.styleClass.addAll("content")
        content.width = 400.0
        content.height = 50.0

        panningWindow.setContent(content)
        stage.scene = Scene(panningWindow,200.0,200.0)
        stage.show()
    }

    @Test
    fun justShow(robot: FxRobot) {
        val content = robot.lookup(".content")
            .queryAs(Rectangle::class.java)

        val testee = robot.lookup(".panning-window")
            .queryAs(PanZoomPanel::class.java)

        Assertions.assertThat(content)
            .satisfies(ThrowingConsumer {
                val point = it.localToScene(Point2D(0.0, 0.0))
                Assertions.assertThat(point.x).isEqualTo(0.0)
                Assertions.assertThat(point.y).isEqualTo(0.0)
            })

        robot.moveTo(testee)
            .press(MouseButton.PRIMARY)
            .moveBy(20.0, 20.0)
            .release(MouseButton.PRIMARY)

        Assertions.assertThat(content)
            .satisfies(ThrowingConsumer {
                val point = it.localToScene(Point2D(0.0, 0.0))
                Assertions.assertThat(point.x).isEqualTo(20.0)
                Assertions.assertThat(point.y).isEqualTo(20.0)
            })

        robot.interact {
            testee.zoom(2.0)
        }

        Assertions.assertThat(content)
            .satisfies(ThrowingConsumer {
                val point = it.localToScene(Point2D(0.0, 0.0))
                Assertions.assertThat(point.x).isEqualTo(30.0)
                Assertions.assertThat(point.y).isEqualTo(30.0)
            })
    }
}