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