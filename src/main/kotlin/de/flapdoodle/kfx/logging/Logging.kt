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

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object Logging {
  fun logger(clazz: KClass<out Any>): KLogger {
    return KLogger(LoggerFactory.getLogger(clazz.java))
  }

  fun logger(clazz: Class<out Any>): KLogger {
    return KLogger(LoggerFactory.getLogger(clazz))
  }
}