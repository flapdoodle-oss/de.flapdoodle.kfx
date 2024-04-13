package de.flapdoodle.kfx.controls.charts

import javafx.scene.paint.Color

data class Serie<X, Y>(
    val label: String,
    val color: Color,
    val values: List<Pair<X, Y>>
) {
}