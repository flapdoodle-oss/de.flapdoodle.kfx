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
package de.flapdoodle.kfx.sampler

import javafx.stage.Stage
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start


@ExtendWith(ApplicationExtension::class)
class RunSamplerIT {

    @Start
    private fun createElement(stage: Stage) {
        val case = 4
        when (case) {
            1 -> PanningWindowsSampler().apply {
                start(stage)
            }
            2 -> SmartTableSampler().apply {
                start(stage)
            }
            3 -> SlimTableSampler().apply {
                start(stage)
            }
            4 -> NodeEditorSampler().apply {
                start(stage)
            }
            10 -> PickMesh3DSampler().apply {
                // -Dprism.forceGPU=true
                start(stage)
            }
            11 -> ComponentsBehaviorSampler().apply {
                start(stage)
            }
        }
    }

    @Test
    @Disabled
    fun waitSomeTime(robot: FxRobot) {
        println("running for one minute...")
        Thread.sleep(2*60*1000)
    }
}