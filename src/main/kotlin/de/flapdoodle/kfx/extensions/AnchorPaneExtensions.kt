package de.flapdoodle.kfx.extensions

import javafx.scene.Node
import javafx.scene.layout.AnchorPane

fun <T: Node> T.withAnchors(
  top: Double? = null,
  left: Double? = null,
  bottom: Double? = null,
  right: Double? = null,
  all: Double? = null
): T {
  if (all!=null) {
    AnchorPane.setTopAnchor(this, all)
    AnchorPane.setLeftAnchor(this, all)
    AnchorPane.setBottomAnchor(this, all)
    AnchorPane.setRightAnchor(this, all)
  }
  if (top != null) AnchorPane.setTopAnchor(this, top)
  if (left != null) AnchorPane.setLeftAnchor(this, left)
  if (bottom != null) AnchorPane.setBottomAnchor(this, bottom)
  if (right != null) AnchorPane.setRightAnchor(this, right)
  return this
}