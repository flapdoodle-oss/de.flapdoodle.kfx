/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Bounds
import javafx.scene.Scene
import javafx.scene.control.Button
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
class NestedPropertyIT {
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

        val testeeLayoutBounds = NestedProperty(SimpleObjectProperty(testee)) { it.layoutBoundsProperty() }
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