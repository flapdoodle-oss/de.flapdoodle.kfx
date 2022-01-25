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

import de.flapdoodle.kfx.layout.grid.WeightGridPane
import javafx.application.Application
import javafx.geometry.HPos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

class WeightGridPaneSampler : Application() {

    override fun start(stage: Stage) {
        stage.scene = Scene(WeightGridPane().apply {
            verticalSpace.set(10.0)
            horizontalSpace.set(20.0)
            
            children.add(Button("test").apply {
                minWidth = 20.0
                maxWidth = 100.0
                WeightGridPane.setPosition(this, 0, 0)
            })
            children.add(Button("test-1").apply {
                WeightGridPane.setPosition(this, 1, 0, horizontalPosition = HPos.RIGHT)
            })
            children.add(Button("test-11").apply {
                WeightGridPane.setPosition(this, 1, 1)
                maxHeight = 100.0
            })

            setColumnWeight(0, 1.0)
            setColumnWeight(1, 2.0)
            setRowWeight(0, 4.0)
            setRowWeight(1, 1.0)
        })
        stage.show()
    }
}