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
package de.flapdoodle.kfx.layout.virtual

import de.flapdoodle.kfx.controls.grapheditor.SizeMode
import de.flapdoodle.kfx.events.SharedEventLock
import de.flapdoodle.kfx.extensions.*
import de.flapdoodle.kfx.sampler.AbsolutePane
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent

class Movables(
    val sharedEventLock: SharedEventLock = SharedEventLock(),
    val regionAsResizeable: (Node) -> Movable<out Node>?
) : AbsolutePane() {

    private var currentEnteredTarget: Movable<out Node>? = null

    init {
        addEventHandler(MouseEvent.ANY, ::handleMouseEvent)
        markAsContainer()
//        boundsInLocalProperty().addListener(InvalidationListener {
//            println("BoundsInLocal: $boundsInLocal")
//        })
    }

    private fun handleMouseEvent(event: MouseEvent) {
        currentEnteredTarget?.let { targetAsRegion ->
            when (event.eventType) {
                MouseEvent.MOUSE_MOVED -> sharedEventLock.ifUnlocked {
                    val targetLocalPosition = targetAsRegion.node.parentToLocal(event.localPosition)
                    val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.size())
                    if (sizeMode != null) {
                        if (sizeMode != SizeMode.INSIDE) {
                            cursor = sizeMode.cursor()
                        } else {
                            cursor = null
                        }
                    }
                }
                MouseEvent.MOUSE_PRESSED -> {
                    if (!event.isControlDown) {
                        sharedEventLock.lock(this) {
                            event.consume()

                            val targetLocalPosition = targetAsRegion.node.parentToLocal(event.localPosition)
                            val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.size())
                            if (sizeMode != null && sizeMode != SizeMode.INSIDE && targetAsRegion.isResizeable()) {
                                cursor = sizeMode.cursor()
                                Action.Resize(
                                    clickPosition = event.screenPosition,
                                    sizeMode = sizeMode,
                                    layout = targetAsRegion.rawLayoutBounds()
                                )
                            } else {
                                cursor = SizeMode.INSIDE.cursor()
                                Action.Move(
                                    clickPosition = event.screenPosition,
                                    layoutPosition = targetAsRegion.node.layoutPosition
                                )
                            }
                        }
                    }
                }
                MouseEvent.MOUSE_DRAGGED -> sharedEventLock.ifLocked(this, Action::class.java) {
                    event.consume()
                    when (it) {
                        is Action.Move -> {
                            val diff = event.screenPosition - it.clickPosition
                            targetAsRegion.node.layoutPosition = it.layoutPosition + targetAsRegion.node.screenDeltaToLocal(diff)
                        }
                        is Action.Resize -> {
                            val diff = event.screenPosition - it.clickPosition
                            val fixedDiff = targetAsRegion.node.screenDeltaToLocal(diff)
                            val resizedBounds = SizeMode.resize(it.sizeMode, it.layout, fixedDiff)
                            targetAsRegion.node.layoutPosition = resizedBounds.layoutPosition
                            targetAsRegion.resizeTo(resizedBounds.size.width, resizedBounds.size.height)
                        }
                    }
                }
                MouseEvent.MOUSE_RELEASED -> sharedEventLock.release(this, Action::class.java) {
                    event.consume()
                    cursor = null
                }
            }
        }

        val target = children.firstOrNull { it == event.target }
        val targetAsRegion = target?.let {
            regionAsResizeable(target)
        }

        if (targetAsRegion != null) {
//            println("event -> $event (lock: ${sharedEventLock.current})")

            when (event.eventType) {
                MouseEvent.MOUSE_ENTERED_TARGET -> sharedEventLock.ifUnlocked {
                    currentEnteredTarget = targetAsRegion

                    val targetLocalPosition = targetAsRegion.node.parentToLocal(event.localPosition)
                    val sizeMode = SizeMode.guess(targetLocalPosition, targetAsRegion.size())
                    if (sizeMode != null) {
                        cursor = sizeMode.cursor()
                    }
                }
                MouseEvent.MOUSE_EXITED_TARGET -> sharedEventLock.ifUnlocked {
                    currentEnteredTarget = null

                    cursor = null
                }
            }
        }
    }

    fun addAll(vararg elements: Node) {
        children.addAll(elements)
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

//    data class Movable(val node: Region, val resizeable: Resizeable?)
}