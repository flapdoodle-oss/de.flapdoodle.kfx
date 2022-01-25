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