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
            windowSize: Double, // >0
            itemSize: Double, // >=0
            itemOffset: Double, // +-
            currentItemOffset: Double
        ): ScrollBounds {
            val fact = itemSize / windowSize // it < 1 if item is smaller

            if (fact<=1) {
                // full visible
                val diff = windowSize - itemSize
                val max = -itemOffset
                val min = max + diff

                val fixedMax = Math.max(min, currentItemOffset)
                val fixedMin = Math.min(max, currentItemOffset)

                val visibleAmount = diff * fact

                return ScrollBounds(fixedMin, fixedMax, visibleAmount)

            } else {
                // partial visible
                val diff = itemSize - windowSize
                val max = -itemOffset
                val min = max - diff

                val fixedMin = Math.min(min, currentItemOffset)
                val fixedMax = Math.max(max, currentItemOffset)

                val visibleAmount = diff / fact

                return ScrollBounds(fixedMin, fixedMax, visibleAmount)
            }
        }
    }
}
