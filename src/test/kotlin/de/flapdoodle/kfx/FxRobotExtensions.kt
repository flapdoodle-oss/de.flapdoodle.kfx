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
package de.flapdoodle.kfx

import javafx.embed.swing.SwingFXUtils
import org.assertj.core.api.Assertions
import org.testfx.service.support.Capture
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.max

fun Capture.matches(javaClass: Class<*>, imageName: String) {
  FxRobotExtensions.assertSameImage(this, javaClass, imageName)
}

object FxRobotExtensions {
  val isHeadless = "Headless".equals(System.getProperty("monocle.platform"))

  fun assertSameImage(capture: Capture, javaClass: Class<*>, baseName: String) {
    val imageName = if (isHeadless) "headless-$baseName" else baseName
    var resource = javaClass.getResource(imageName)
    if (null == resource) {
      resource = javaClass.getResource(baseName)
    }
    if (resource != null) {
      val expected = requireNotNull(ImageIO.read(resource)) { "could not read image from $resource" }
      val current = bufferedImage(capture)
      val diff = diff(expected, current)
      if (diff != null) {
        val enhancedDiff = enhance(diff)
        val tempDir = tempDir()
        val currentInTemp = write(tempDir, "current-$baseName", current)
        val diffInTemp = write(tempDir, "diff-$baseName", diff)
        val enhancedDiffInTemp = write(tempDir, "enhanced-diff-$baseName", enhancedDiff)
        Assertions.fail<Unit>("current $currentInTemp does not match $resource, see diff in $diffInTemp ($enhancedDiffInTemp)")
      }
    } else {
      val current = bufferedImage(capture)
      val destination = write(imageName, current)
      Assertions.fail<Unit>("expected image for $javaClass/$imageName: $destination")
    }
  }

  private fun tempDir(): Path {
    return Files.createTempDirectory("javafx-test")
  }

  private fun write(imageName: String, current: BufferedImage): Path {
    return write(tempDir(), imageName, current)
  }

  private fun write(tempDir: Path, imageName: String, current: BufferedImage): Path {
    val destination = tempDir.resolve(imageName)
    val success = ImageIO.write(current, fileType(imageName), destination.toFile())
    require(success) { "could not write image to $destination" }
    return destination
  }

  private fun diff(expected: BufferedImage, current: BufferedImage): BufferedImage? {
    val width = max(expected.width, current.width)
    val height = max(expected.height, current.height)
    val ret = bufferedImage(width, height)
    var diff = false
    (0.until(width)).forEach { x ->
      (0.until(height)).forEach { y ->
        val rgbDiff = rgbDiff(getRGB(expected, x, y), getRGB(current, x, y))
        ret.setRGB(x, y, rgbDiff)
        if (rgbDiff != 0) diff = true
      }
    }
    return if (diff) ret else null
  }

  private fun enhance(diff: BufferedImage): BufferedImage {
    val ret = bufferedImage(diff.width, diff.height)
    var maxR = 0
    var maxG = 0
    var maxB = 0
    (0.until(ret.width)).forEach { x ->
      (0.until(ret.height)).forEach { y ->
        val pixel = getRGB(diff, x, y)
        maxR = max(maxR, (pixel shr 16) and 0xFF)
        maxG = max(maxG, (pixel shr 8) and 0xFF)
        maxB = max(maxB, pixel and 0xFF)
      }
    }
    val factorR = 255 / maxR
    val factorG = 255 / maxG
    val factorB = 255 / maxB
    (0.until(ret.width)).forEach { x ->
      (0.until(ret.height)).forEach { y ->
        val pixel = getRGB(diff, x, y)
        val r = ((pixel shr 16) and 0xFF) * factorR
        val g = ((pixel shr 8) and 0xFF) * factorG
        val b = (pixel and 0xFF) * factorB
        ret.setRGB(x, y, (r shl 16) + (g shl 8) + b)
      }
    }
    return ret
  }

  private fun getRGB(image: BufferedImage, x: Int, y: Int): Int {
    return if (x < image.width && y < image.height) image.getRGB(x, y) else 0
  }

  private fun rgbDiff(a: Int, b: Int): Int {
    if (a == b) {
      return 0
    }
    //val alpha: Int = (a shr 24) and 0xFF

    val a_red: Int = (a shr 16) and 0xFF
    val a_green: Int = (a shr 8) and 0xFF
    val a_blue: Int = (a) and 0xFF

    val b_red: Int = (b shr 16) and 0xFF
    val b_green: Int = (b shr 8) and 0xFF
    val b_blue: Int = (b) and 0xFF

    val diff_red = abs(b_red - a_red)
    val diff_green = abs(b_green - a_green)
    val diff_blue = abs(b_blue - a_blue)

    return (diff_red shl 16) + (diff_green shl 8) + diff_blue
  }

  private fun fileType(imageName: String): String {
    if (imageName.endsWith(".png")) return "png"
    throw IllegalArgumentException("file extension not supported: $imageName")
  }

  private fun bufferedImage(capture: Capture): BufferedImage {
    val image = requireNotNull(capture.image) { "could not get image from $capture" }
    val destination = bufferedImage(image.width.toInt(), image.height.toInt())
    SwingFXUtils.fromFXImage(image, destination)
    return destination
  }

  private fun bufferedImage(width: Int, height: Int): BufferedImage {
    return BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  }
}