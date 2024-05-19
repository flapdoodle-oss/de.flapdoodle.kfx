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