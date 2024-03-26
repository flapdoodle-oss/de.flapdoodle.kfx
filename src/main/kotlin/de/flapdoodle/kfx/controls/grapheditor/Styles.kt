package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import javafx.css.PseudoClass
import javafx.scene.Node

object Styles {
//  val Even = PseudoClassWrapper<Node>(PseudoClass.getPseudoClass("even"))
//  val Readonly = PseudoClassWrapper<Node>(PseudoClass.getPseudoClass("readonly"))
  val Focused = PseudoClassWrapper<Vertex>(PseudoClass.getPseudoClass("focused"))
  val Selected = PseudoClassWrapper<Vertex>(PseudoClass.getPseudoClass("selected"))
}