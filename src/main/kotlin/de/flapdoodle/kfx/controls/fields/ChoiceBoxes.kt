package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.i18n.I18NEnumStringConverter
import de.flapdoodle.kfx.i18n.I18NTypeStringConverter
import javafx.scene.control.ChoiceBox
import java.util.*
import kotlin.reflect.KClass

object ChoiceBoxes {
  fun <T : KClass<out Any>> forTypes(
    locale: Locale,
    bundleName: String,
    classes: List<T>,
    default: T? = null
  ): ChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ChoiceBox<T>().apply {
      items.addAll(classes)
      value = default
      converter = I18NTypeStringConverter(locale, bundleName)
    }
  }

  fun <T : Enum<T>> forEnums(
    locale: Locale,
    bundleName: String,
    enumType: KClass<T>,
    classes: List<T> = EnumSet.allOf(enumType.java).toList(),
    default: T? = null
  ): ChoiceBox<T> {
    require(default == null || classes.contains(default)) { "default value $default is not in selection: $classes" }

    return ChoiceBox<T>().apply {
      items.addAll(classes)
      value = default
      converter = I18NEnumStringConverter(locale, bundleName, enumType)
    }
  }
}