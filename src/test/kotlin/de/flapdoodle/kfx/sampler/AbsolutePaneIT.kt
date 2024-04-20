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
package de.flapdoodle.kfx.sampler

import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ThrowingConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class AbsolutePaneIT {

    @Start
    private fun createElement(stage: Stage) {
        val testee = AbsolutePane()
        testee.styleClass.add("testee")
        testee.children.add(Rectangle(100.0, 100.0).apply {
            layoutX = -10.0
            layoutY = -30.0
            fill = Color.BLUE
        })
        val anchor=AnchorPane()
        anchor.children.add(testee)
        stage.scene = Scene(anchor,200.0,200.0)
        stage.show()
    }

    @Test
    fun justShow(robot: FxRobot) {
        assertThat(robot.lookup(".testee").queryAs(AbsolutePane::class.java))
            .extracting { it.boundsInParent }
            .satisfies(ThrowingConsumer {
                assertThat(it.minX).describedAs("minX").isEqualTo(-10.0)
                assertThat(it.maxX).describedAs("maxX").isEqualTo(90.0)
                assertThat(it.minY).describedAs("minY").isEqualTo(-30.0)
                assertThat(it.maxY).describedAs("maxY").isEqualTo(70.0)
            })
    }
}