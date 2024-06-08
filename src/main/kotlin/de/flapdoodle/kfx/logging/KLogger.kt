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
package de.flapdoodle.kfx.logging

import org.apache.logging.log4j.Logger

class KLogger(
  val wrapped: Logger
) {
  inline fun trace(message: () -> String) {
    if (wrapped.isTraceEnabled) wrapped.trace(message())
  }

  inline fun trace(message: String, throwable: Throwable) {
    wrapped.trace(message, throwable)
  }

  inline fun debug(message: () -> String) {
    if (wrapped.isDebugEnabled) wrapped.debug(message())
  }

  inline fun debug(message: String, throwable: Throwable) {
    wrapped.debug(message, throwable)
  }

  inline fun info(message: () -> String) {
    if (wrapped.isInfoEnabled) wrapped.info(message())
  }

  inline fun info(message: String, throwable: Throwable) {
    wrapped.info(message, throwable)
  }

  inline fun warning(message: () -> String) {
    if (wrapped.isWarnEnabled) wrapped.warn(message())
  }

  inline fun warning(message: String, throwable: Throwable) {
    wrapped.warn(message, throwable)
  }

  inline fun error(message: () -> String) {
    if (wrapped.isErrorEnabled) wrapped.error(message())
  }

  inline fun error(message: String, throwable: Throwable) {
    wrapped.error(message, throwable)
  }

  inline fun fatal(message: () -> String) {
    if (wrapped.isFatalEnabled) wrapped.fatal(message())
  }

  inline fun fatal(message: String, throwable: Throwable) {
    wrapped.fatal(message, throwable)
  }

}