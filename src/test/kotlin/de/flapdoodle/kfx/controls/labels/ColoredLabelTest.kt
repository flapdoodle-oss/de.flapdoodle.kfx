package de.flapdoodle.kfx.controls.labels

import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColoredLabelTest {
  @Test
  fun single() {
    val text = "123456"
    val parts = listOf(
      ColoredLabel.Part(2, 4, Color.RED)
    )

    assertThat(ColoredLabel.coloredText(text, parts))
      .containsExactly(
        "12" to null,
        "34" to Color.RED,
        "56" to null,
      )
  }

  @Test
  fun sample() {
    val text = "This is a sample text."
    val parts = listOf(
      ColoredLabel.Part(5, 16, Color.RED),
      ColoredLabel.Part(10, 16, Color.GREEN),
      ColoredLabel.Part(0, 4, Color.BLUE),
      ColoredLabel.Part(18, 32, Color.YELLOW),
    )

    assertThat(ColoredLabel.coloredText(text, parts))
      .containsExactly(
        "This" to Color.BLUE,
        " " to null,
        "is a " to Color.RED,
        "sample" to Color.RED.interpolate(Color.GREEN, 0.5),
        " t" to null,
        "ext." to Color.YELLOW
      )
  }
}