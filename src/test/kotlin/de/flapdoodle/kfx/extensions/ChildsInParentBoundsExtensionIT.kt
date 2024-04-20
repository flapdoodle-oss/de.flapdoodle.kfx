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
package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.hasBounds
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class ChildsInParentBoundsExtensionIT {
    @Start
    private fun createElement(stage: Stage) {
        val root = Pane()
        root.markAsContainer()

        root.styleClass.add("root")
        stage.scene = Scene(root,200.0,200.0)
        stage.show()
    }

    @Test
    fun rectangleInMovedPaneCenteredAt00(robot: FxRobot) {
        val root = robot.lookup(".root").queryAs(Pane::class.java)
        root.markAsContainer()
        
        val movedPane = Pane().apply {
            layoutX = 20.0
            layoutY = 20.0
            prefWidth = 50.0
            prefHeight = 50.0
            markAsContainer()
        }

        val rectangle = Rectangle(30.0, 30.0).apply {
            layoutX = -15.0
            layoutY = -15.0
        }

        robot.interact {
            root.children.add(movedPane)
            movedPane.children.add(rectangle)
        }

        assertThat(root.boundsInLocal)
            .hasBounds(LayoutBounds(0.0, 0.0, 200.0, 200.0))

        assertThat(rectangle.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(0.0, 0.0, 30.0, 30.0))
        assertThat(rectangle.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-15.0, -15.0, 30.0, 30.0))

        assertThat(movedPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(movedPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))

        assertThat(root.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(root.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
    }

    @Test
    fun rectangleIn2MovedPanesCenteredAt00(robot: FxRobot) {
        val root = robot.lookup(".root").queryAs(Pane::class.java)
        root.markAsContainer()

        val movedDownPane = Pane().apply {
            layoutX = 40.0
            layoutY = 40.0
            prefWidth = 50.0
            prefHeight = 50.0
            markAsContainer()
        }

        val movedUpPane = Pane().apply {
            layoutX = -20.0
            layoutY = -20.0
            prefWidth = 50.0
            prefHeight = 50.0
            markAsContainer()
        }

        val rectangle = Rectangle(30.0, 30.0).apply {
            layoutX = -15.0
            layoutY = -15.0
        }

        robot.interact {
            root.children.add(movedDownPane)
            movedDownPane.children.add(movedUpPane)
            movedUpPane.children.add(rectangle)
        }

        assertThat(root.boundsInLocal)
            .hasBounds(LayoutBounds(0.0, 0.0, 200.0, 200.0))

        assertThat(rectangle.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(0.0, 0.0, 30.0, 30.0))
        assertThat(rectangle.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-15.0, -15.0, 30.0, 30.0))

        assertThat(movedUpPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(-35.0, -35.0, 30.0, 30.0))
        assertThat(movedUpPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-35.0, -35.0, 30.0, 30.0))

        assertThat(movedDownPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(movedDownPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))

        assertThat(root.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(root.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
    }

    @Test
    fun rectangleIn2MovedPanesCenteredAt00AndMovedAgain(robot: FxRobot) {
        val root = robot.lookup(".root").queryAs(Pane::class.java)
        root.markAsContainer()

        val movedDownPane = Pane().apply {
            layoutX = 40.0
            layoutY = 40.0
            prefWidth = 50.0
            prefHeight = 50.0
            markAsContainer()
        }

        val movedUpPane = Pane().apply {
            layoutX = -20.0
            layoutY = -20.0
            prefWidth = 50.0
            prefHeight = 50.0
            markAsContainer()
        }

        val rectangle = Rectangle(30.0, 30.0).apply {
            layoutX = -15.0
            layoutY = -15.0
        }

        robot.interact {
            root.children.add(movedDownPane)
            movedDownPane.children.add(movedUpPane)
            movedUpPane.children.add(rectangle)
        }

        assertThat(root.boundsInLocal)
            .hasBounds(LayoutBounds(0.0, 0.0, 200.0, 200.0))

        assertThat(rectangle.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(0.0, 0.0, 30.0, 30.0))
        assertThat(rectangle.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-15.0, -15.0, 30.0, 30.0))

        assertThat(movedUpPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(-35.0, -35.0, 30.0, 30.0))
        assertThat(movedUpPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-35.0, -35.0, 30.0, 30.0))

        assertThat(movedDownPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(movedDownPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))

        assertThat(root.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))
        assertThat(root.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(5.0, 5.0, 30.0, 30.0))

        robot.interact {
            movedUpPane.layoutX = movedUpPane.layoutX + 10.0
            movedUpPane.layoutY = movedUpPane.layoutY - 5.0
        }
        
        assertThat(root.boundsInLocal)
            .hasBounds(LayoutBounds(0.0, 0.0, 200.0, 200.0))

        assertThat(rectangle.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(0.0, 0.0, 30.0, 30.0))
        assertThat(rectangle.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-15.0, -15.0, 30.0, 30.0))

        assertThat(movedUpPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(-35.0, -35.0, 30.0, 30.0))
        assertThat(movedUpPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(-25.0, -40.0, 30.0, 30.0))

        assertThat(movedDownPane.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(15.0, 0.0, 30.0, 30.0))
        assertThat(movedDownPane.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(15.0, 0.0, 30.0, 30.0))

        assertThat(root.containerlessBoundsInLocal())
            .hasBounds(LayoutBounds(15.0, 0.0, 30.0, 30.0))
        assertThat(root.containerlessBoundsInParent())
            .hasBounds(LayoutBounds(15.0, 0.0, 30.0, 30.0))

    }
}