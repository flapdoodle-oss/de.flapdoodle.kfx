package de.flapdoodle.kfx.extensions

import javafx.scene.Node
import javafx.scene.Parent

fun Node.cssClassName(vararg name: String) {
  styleClass.addAll(name)
}

fun Parent.bindCss(name: String) {
  cssClassName(name)
  val resource = javaClass.getResource("${javaClass.simpleName}.css")
  require(resource!=null) { "could not bind css to ${javaClass.simpleName}.css" }
  stylesheets += resource.toExternalForm()
}