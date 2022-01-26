/**
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