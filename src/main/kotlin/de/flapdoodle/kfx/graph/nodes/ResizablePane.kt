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
package de.flapdoodle.kfx.graph.nodes

import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.types.LayoutBounds
import de.flapdoodle.kfx.types.rawLayoutBounds
import de.flapdoodle.kfx.types.size
import javafx.geometry.Point2D
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle

@Deprecated("use Movables layer")
class ResizablePane(val sharedEventLock: SharedEventLock = SharedEventLock()) : StackPane() {
    init {
        isPickOnBounds = false

        addEventHandler(MouseEvent.ANY, this::handleMouseEvent)

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
        this.layoutX = 20.0
        this.layoutY = 30.0
    }

    private fun handleMouseEvent(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_MOVED -> sharedEventLock.ifUnlocked {
                val sizeMode = SizeMode.guess(event.localPosition, size)
                if (sizeMode != null) {
                    cursor = sizeMode.cursor()
                }
            }
            MouseEvent.MOUSE_EXITED -> sharedEventLock.ifUnlocked {
                cursor = null
            }
            MouseEvent.MOUSE_PRESSED -> {
                if (!event.isControlDown) {
                    event.consume()
                    sharedEventLock.lock(this) {
                        event.consume()
                        val sizeMode = SizeMode.guess(event.localPosition, size)
                        if (sizeMode != null && sizeMode != SizeMode.INSIDE) {
                            cursor = sizeMode.cursor()
                            Action.Resize(
                                clickPosition = event.screenPosition,
                                sizeMode = sizeMode,
                                layout = rawLayoutBounds
                            )
                        } else
                            Action.Move(
                                clickPosition = event.screenPosition,
                                layoutPosition = layoutPosition
                            )
                    }
                }
            }
            MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                event.consume()
                when (it) {
                    is Action.Move -> {
                        val diff = event.screenPosition - it.clickPosition
                        layoutPosition = it.layoutPosition + screenDeltaToLocal(diff)
                    }
                    is Action.Resize -> {
                        val diff = event.screenPosition - it.clickPosition
                        val fixedDiff = screenDeltaToLocal(diff)
                        val resizedBounds = SizeMode.resize(it.sizeMode, it.layout, fixedDiff)
                        this.layoutPosition = resizedBounds.layoutPosition
                        this.width = resizedBounds.size.width
                        this.height = resizedBounds.size.height
                    }
                }
            }
            MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                event.consume()
                cursor = null
            }
        }
    }

    sealed class Action {
        data class Move(
            val clickPosition: Point2D,
            val layoutPosition: Point2D
        ) : Action()

        data class Resize(
            val clickPosition: Point2D,
            val sizeMode: SizeMode,
            val layout: LayoutBounds
        ) : Action()
    }
}