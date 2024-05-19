package de.flapdoodle.kfx.extensions

import javafx.scene.Parent
import kotlin.reflect.KClass

object Parents {
  fun bindCss(parent: Parent, name: String) {
    parent.cssClassName(name)
    val clazz = parent.javaClass

    val resource = clazz.getResource("${clazz.simpleName}.css")
    require(resource!=null) { "could not bind css to ${clazz.simpleName}.css" }

    parent.stylesheets += resource.toExternalForm()
  }

  fun bindCss(parent: Parent, context: KClass<out Any>, name: String) {
    parent.cssClassName(name)
    val clazz = context.java

    val resource = clazz.getResource("${clazz.simpleName}.css")
    require(resource!=null) { "could not bind css to ${clazz.simpleName}.css" }

    parent.stylesheets += resource.toExternalForm()
  }
}