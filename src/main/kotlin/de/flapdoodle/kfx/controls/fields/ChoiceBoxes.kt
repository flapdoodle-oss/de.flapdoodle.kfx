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
package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.i18n.I18NEnumStringConverter
import de.flapdoodle.kfx.i18n.I18NTypeStringConverter
import de.flapdoodle.kfx.i18n.ResourceBundleWrapper
import javafx.scene.control.ChoiceBox
import java.util.*
import kotlin.reflect.KClass

object ChoiceBoxes {
  @Deprecated("use validateChoiceBox")
  fun <T : KClass<out Any>> forTypes(
    resourceBundle: ResourceBundleWrapper,
    classes: List<T>,
    default: T? = null
  ): ChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ChoiceBox<T>().apply {
      items.addAll(classes)
      value = default
      converter = I18NTypeStringConverter(resourceBundle)
    }
  }

  fun <T : KClass<out Any>> forTypes(
    resourceBundle: ResourceBundleWrapper,
    classes: List<T>,
    default: T? = null,
    validate: (T?) -> String?
  ): ValidatingChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ValidatingChoiceBox(classes, default, I18NTypeStringConverter(resourceBundle), validate)
  }


  @Deprecated("use validateChoiceBox")
  fun <T : Enum<T>> forEnums(
    resourceBundle: ResourceBundleWrapper,
    enumType: KClass<T>,
    default: T? = null,
    classes: List<T> = EnumSet.allOf(enumType.java).toList()
  ): ChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ChoiceBox<T>().apply {
      items.addAll(classes)
      value = default
      converter = I18NEnumStringConverter(resourceBundle, enumType)
    }
  }

  fun <T : Enum<T>> forEnums(
    resourceBundle: ResourceBundleWrapper,
    enumType: KClass<T>,
    classes: List<T> = EnumSet.allOf(enumType.java).toList(),
    default: T? = null,
    validate: (T?) -> String?
  ): ValidatingChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ValidatingChoiceBox(classes, default, I18NEnumStringConverter(resourceBundle, enumType), validate)
  }
}