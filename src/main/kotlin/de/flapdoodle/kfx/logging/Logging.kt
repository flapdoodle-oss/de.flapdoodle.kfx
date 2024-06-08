package de.flapdoodle.kfx.logging

import org.apache.logging.log4j.LogManager
import kotlin.reflect.KClass

object Logging {
  fun logger(clazz: KClass<out Any>): KLogger {
    return KLogger(LogManager.getLogger(clazz.java))
  }
}