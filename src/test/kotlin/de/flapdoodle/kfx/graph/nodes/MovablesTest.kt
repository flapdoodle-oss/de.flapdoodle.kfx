package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.extensions.size
import de.flapdoodle.kfx.isNearlyEqualTo
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import org.testfx.robot.Motion

@ExtendWith(ApplicationExtension::class)
internal class MovablesTest {

    @Start
    private fun createElement(stage: Stage) {
        val testee = Movables { node ->
            when (node) {
                is Rectangle -> Movable(node, { rect -> Dimension2D(rect.width, rect.height) }, { it,w,h ->
                    it.width = w
                    it.height = h
                })
                else -> null
            }
        }
        val content = Rectangle()
        content.styleClass.addAll("movable")
        content.fill = Color.GRAY
        content.width = 50.0
        content.height = 50.0

        testee.children.addAll(content)
        stage.scene = Scene(testee, 200.0, 200.0)
        stage.show()
    }

    @Test
    fun moveMovable(robot: FxRobot) {
        val testee = robot.lookup<Node> { it is Movables }
            .queryAs(Movables::class.java)

        val content = robot.lookup(".movable")
            .queryAs(Rectangle::class.java)

        robot.moveTo(content)

        robot.moveBy(10.0, 10.0)
            .press(MouseButton.PRIMARY)
            .moveBy(10.0, 25.0)
            .release(MouseButton.PRIMARY)

        assertThat(content.layoutPosition)
            .isNearlyEqualTo(Point2D(10.0, 25.0))
        assertThat(content.size)
            .isEqualTo(Dimension2D(50.0, 50.0))

        assertThat(testee.layoutPosition)
            .isNearlyEqualTo(Point2D(0.0, 0.0))
    }

    @Test
    fun moveAndResize(robot: FxRobot) {
        val testee = robot.lookup<Node> { it is Movables }
            .queryAs(Movables::class.java)

        val content = robot.lookup(".movable")
            .queryAs(Rectangle::class.java)

        robot.moveTo(content)

        robot.moveBy(10.0, 10.0)
            .press(MouseButton.PRIMARY)
            .moveBy(50.0, 50.0)
            .release(MouseButton.PRIMARY)

        assertThat(content.layoutPosition)
            .isNearlyEqualTo(Point2D(50.0, 50.0))

        robot.moveTo(robot.point(testee.scene).atPosition(Pos.TOP_LEFT), Motion.DEFAULT)

        robot.moveBy(50.0, 50.0)
            .press(MouseButton.PRIMARY)
            .moveBy(-10.0,-30.0)
            .release(MouseButton.PRIMARY)

        assertThat(content.layoutPosition)
            .isNearlyEqualTo(Point2D(40.0, 20.0))
        assertThat(content.size)
            .isEqualTo(Dimension2D(60.0, 80.0))
    }
}