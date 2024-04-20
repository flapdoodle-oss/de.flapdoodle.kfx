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