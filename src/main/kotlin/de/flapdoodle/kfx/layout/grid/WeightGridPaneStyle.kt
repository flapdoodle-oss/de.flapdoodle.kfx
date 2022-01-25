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

import javafx.css.CssMetaData
import javafx.css.StyleablePropertyFactory
import javafx.scene.control.Control

class WeightGridPaneStyle {
    companion object {
        internal val CSS_HSPACE_NAME = "weighted-grid-horizontal-space"
        internal val CSS_VSPACE_NAME = "weighted-grid-vertical-space"

        private val FACTORY = StyleablePropertyFactory<WeightGridPane>(Control.getClassCssMetaData())

        internal val CSS_HSPACE: CssMetaData<WeightGridPane, Number> = FACTORY.createSizeCssMetaData(
            CSS_HSPACE_NAME,
            { it.horizontalSpace },
            2.0)

        internal val CSS_VSPACE: CssMetaData<WeightGridPane, Number> = FACTORY.createSizeCssMetaData(
            CSS_VSPACE_NAME,
            { it.verticalSpace },
            2.0)

        internal val CONTROL_CSS_META_DATA = (FACTORY.cssMetaData + CSS_HSPACE + CSS_VSPACE)
    }
}