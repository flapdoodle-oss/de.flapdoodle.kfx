package de.flapdoodle.kswing

import javafx.scene.Node
import java.awt.Component

fun Component.widthLimits(): Pair<Int, Int> {
  val minW = minimumSize.width
  return if (true /*this.isResizable*/) {
    val maxW = maximumSize.width
    Pair(minW, if (maxW>0.0) maxW else Int.MAX_VALUE)
  } else {
    Pair(minW, minW)
  }
}

fun Component.heightLimits(): Pair<Int, Int> {
  val minH = minimumSize.width
  return if (true /*isResizable*/) {
    val maxH = maximumSize.height
    Pair(minH, if (maxH>0.0) maxH else Int.MAX_VALUE)
  } else {
    Pair(minH, minH)
  }
}
