package de.flapdoodle.kfx.extensions

import javafx.geometry.Dimension2D


operator fun Dimension2D.minus(other: Dimension2D): Dimension2D {
    return Dimension2D(this.width - other.width, this.height - other.height)
}

operator fun Dimension2D.plus(other: Dimension2D): Dimension2D {
    return Dimension2D(this.width + other.width, this.height + other.height)
}

fun Dimension2D.addWidth(value: Double): Dimension2D = Dimension2D(this.width + value, this.height)
fun Dimension2D.addHeight(value: Double): Dimension2D = Dimension2D(this.width, this.height + value)
fun Dimension2D.subWidth(value: Double): Dimension2D = addWidth(-value)
fun Dimension2D.subHeight(value: Double): Dimension2D = addHeight(-value)


