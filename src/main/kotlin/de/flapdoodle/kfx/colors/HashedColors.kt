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
package de.flapdoodle.kfx.colors

import javafx.scene.paint.Color

object HashedColors {

    private val colorNames = arrayOf(
        "#ffffff", "#fb6b1d", "#e83b3b", "#831c5d", "#c32454", "#f04f78", "#f68181", "#fca790", "#e3c896",
        "#ab947a", "#966c6c", "#625565", "#3e3546", "#0b5e65", "#0b8a8f", "#1ebc73", "#91db69", "#fbff86",
        "#fbb954", "#cd683d", "#9e4539", "#7a3045", "#6b3e75", "#905ea9", "#a884f3", "#eaaded", "#8fd3ff",
        "#4d9be6", "#4d65b4", "#484a77", "#30e1b9", "#8ff8e2"
    )

    private val colors8dark = arrayOf("#a82800","#a57e00","#7fa602","#09a70d","#046c9d","#04189f","#4504a3","#ab0a80")
    private val colors8bright = arrayOf("#f13501","#ffe201","#baf004","#0bf110","#04a0e4","#0426e7","#5e05ed","#f50ebe")
    private val colorNames8 = colors8dark.zip(colors8bright).flatMap { listOf(it.first, it.second) }

    private val colors = colorNames8.map(Color::web)

    fun hashedColor(name: String) = hashedColor(name.hashCode())
    fun hashedColor(hash: Int): Color {
        val index = (if (hash > 0) hash else -hash) % colors.size
        return colors[index]
    }

}