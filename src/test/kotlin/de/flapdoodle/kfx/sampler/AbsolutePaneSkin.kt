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

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.control.SkinBase

class AbsolutePaneSkin(val control: AbsolutePane) : SkinBase<AbsolutePane>(control) {

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
        children.forEach { child ->
            if (child.isManaged) {
                val x = child.layoutX
                val y = child.layoutY
                val w = child.prefWidth(contentHeight)
                val h = child.prefHeight(contentWidth)
                layoutInArea(child, x, y, w, h, -1.0, HPos.CENTER, VPos.CENTER)
            }
        }
    }
}