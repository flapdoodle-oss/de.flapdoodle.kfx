package de.flapdoodle.kfx.logging

import org.apache.logging.log4j.Logger

class KLogger(
  val wrapped: Logger
) {

  inline fun debug(message: () -> String) {
    if (wrapped.isDebugEnabled) wrapped.debug(message())
  }

  inline fun warning(message: () -> String) {
    if (wrapped.isWarnEnabled) wrapped.warn(message())
  }
}