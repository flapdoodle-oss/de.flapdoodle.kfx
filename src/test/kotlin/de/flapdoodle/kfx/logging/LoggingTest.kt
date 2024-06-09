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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class LoggingTest {

  @Test
  fun logThisPackage() {
    logPackage("de.flapdoodle.kfx.logging")
  }

  @Test
  fun logKfxPackage() {
    logPackage("de.flapdoodle.kfx")
  }

  @Test
  fun logDePackage() {
    logPackage("de")
  }

  private fun logPackage(name: String) {
    val logger = LoggerFactory.getLogger(name)
    logger.error("error: log in $name")
    logger.info("info: log in $name")
    logger.debug("debug: log in $name")
  }
}