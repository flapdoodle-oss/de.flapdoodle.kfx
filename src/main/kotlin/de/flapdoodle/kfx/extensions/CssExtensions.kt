package de.flapdoodle.kfx.extensions

import javafx.scene.Node
import javafx.scene.Parent

fun Node.cssClassName(vararg name: String) {
  styleClass.addAll(name)
}

fun Parent.bindCss(name: String) {
  cssClassName(name)
  stylesheets += javaClass.getResource(javaClass.simpleName+".css").toExternalForm()
}