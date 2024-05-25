package de.flapdoodle.kfx.bindings.node

import de.flapdoodle.kfx.controls.fields.TypedTextField
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Bounds
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import java.util.function.Predicate

@ExtendWith(ApplicationExtension::class)
class NodePropertyIT {
    @Start
    private fun createElement(stage: Stage) {
        val testee = Button().apply {
            id = "testee"
        }
        val pane = Pane().apply {
            id = "pane"
            children.add(testee)
        }
        stage.scene = Scene(pane,200.0,200.0)
        stage.show()
    }

    @Test
    fun justShow(robot: FxRobot) {
        val pane = robot.lookup(Predicate { it.id=="pane" }).queryAs(Pane::class.java)
        val testee = robot.lookup(Predicate { it.id=="testee" }).queryAs(Button::class.java)

        val testeeLayoutBounds = NodeProperty(SimpleObjectProperty(testee)) { it.layoutBoundsProperty() }
        var testeeLayouts = emptyList<Bounds>()
        testeeLayoutBounds.addListener { _, _, layout ->
            testeeLayouts = if (layout!=null) testeeLayouts + layout else testeeLayouts
        }

        robot.clickOn(testee)

        assertThat(testeeLayouts).isEmpty()
        testee.resizeRelocate(10.0, 10.0, 30.0, 50.0)
        val lastBound = testee.layoutBoundsProperty().value

        assertThat(testeeLayouts).hasSize(2)
        assertThat(testeeLayouts[1]).isEqualTo(lastBound)
    }

}