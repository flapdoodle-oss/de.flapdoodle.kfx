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

import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle

class NonResizablePane() : StackPane() {
    init {
        isPickOnBounds = false

        val border = Rectangle()
        val background = Rectangle()

        background.widthProperty().bind(border.widthProperty().subtract(border.strokeWidthProperty().multiply(2)))
        background.heightProperty().bind(border.heightProperty().subtract(border.strokeWidthProperty().multiply(2)))

        border.widthProperty().bind(this.widthProperty())
        border.heightProperty().bind(this.heightProperty())

        border.getStyleClass()
            .setAll("default-node-border")
        background.getStyleClass()
            .setAll("default-node-background")

        this.getChildren().addAll(border, background)
        this.setMinSize(
            30.0,
            30.0
        )
        this.resize(60.0, 60.0)
//        this.layoutX = 20.0
//        this.layoutY = 30.0
    }

    fun resizeTo(width: Double, height: Double) {
        this.width=width
        this.height=height
    }
}