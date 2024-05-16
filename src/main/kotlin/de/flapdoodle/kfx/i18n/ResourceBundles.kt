package de.flapdoodle.kfx.i18n

import java.util.*

object ResourceBundles {
  private val exceptions = I18N.resourceBundle(Locale.getDefault(), "de.flapdoodle.kfx.exceptions")
  fun exceptions() = exceptions
}