package de.flapdoodle.kfx.extensions

import javafx.geometry.BoundingBox
import javafx.scene.Node

object BoundingBoxes {
    fun empty() = BoundingBox(0.0, 0.0, 0.0, -1.0, -1.0,-1.0)

    fun merge(a: javafx.geometry.Bounds, b: javafx.geometry.Bounds): javafx.geometry.Bounds {
        if (a.isEmpty) return b
        if (b.isEmpty) return a

        val minX = Math.min(a.minX, b.minX)
        val minY = Math.min(a.minY, b.minY)
        val minZ = Math.min(a.minZ, b.minZ)
        val maxWidth = Math.max(a.maxX, b.maxX) - minX
        val maxHeight = Math.max(a.maxY, b.maxY) - minY
        val maxDepth = Math.max(a.maxZ, b.maxZ) - minZ

        return BoundingBox(
            minX,
            minY,
            minZ,
            maxWidth,
            maxHeight,
            maxDepth
        )
    }

    fun reduce(bounds: List<javafx.geometry.Bounds>): javafx.geometry.Bounds {
        return when (bounds.size) {
            0 -> empty()
            1 -> bounds[0]
            else -> bounds.reduce { a, b -> BoundingBoxes.merge(a,b)}
        }
    }
}