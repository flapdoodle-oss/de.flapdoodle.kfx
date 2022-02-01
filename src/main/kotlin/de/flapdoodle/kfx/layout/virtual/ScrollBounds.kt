package de.flapdoodle.kfx.layout.virtual

import javafx.scene.control.ScrollBar

fun ScrollBar.setBounds(scrollBounds: ScrollBounds) {
    this.min = scrollBounds.min
    this.max = scrollBounds.max
    this.visibleAmount = scrollBounds.visibleAmount
}

data class ScrollBounds(
    val min: Double,
    val max: Double,
    val visibleAmount: Double
    ) {

    companion object {
        fun of(
            windowSize: Double,
            itemSize: Double,
            itemOffset: Double,
            currentItemOffset: Double
        ): ScrollBounds {
            val diff = itemSize - windowSize // it < 0 if item is smaller
            val fact = itemSize / windowSize // it < 1 if item is smaller

            val min = itemOffset
            val max = min + diff

            var fixedMin = Math.min(min, currentItemOffset)
            var fixedMax = Math.max(max, currentItemOffset)

            if (fact <= 1) {
                fixedMax = Math.max(min, currentItemOffset)
                fixedMin = Math.min(max, currentItemOffset)
            }

            val visibleAmount = if (fact > 1) (diff) / fact else (diff) * fact * -1.0

            return ScrollBounds(fixedMin, fixedMax, Math.abs(visibleAmount))
        }
    }
}
