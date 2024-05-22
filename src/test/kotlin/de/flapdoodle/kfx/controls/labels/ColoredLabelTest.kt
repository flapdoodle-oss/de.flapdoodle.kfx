package de.flapdoodle.kfx.controls.labels

import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ColoredLabelTest {
  @Test
  fun single() {
    val parts = listOf(
      ColoredLabel.Part(2, 4, Color.RED)
    )

    assertThat(ColoredLabel.coloredText("123", parts))
      .containsExactly(
        ColoredLabel.ColoredText(0,"12"),
        ColoredLabel.ColoredText(1, "3", Color.RED),
      )

    assertThat(ColoredLabel.coloredText("1234", parts))
      .containsExactly(
        ColoredLabel.ColoredText(0, "12"),
        ColoredLabel.ColoredText(1, "34", Color.RED),
      )

    assertThat(ColoredLabel.coloredText("12345", parts))
      .containsExactly(
        ColoredLabel.ColoredText(0, "12"),
        ColoredLabel.ColoredText(1,"34",Color.RED),
        ColoredLabel.ColoredText(2, "5"),
      )

    assertThat(ColoredLabel.coloredText("123456", parts))
      .containsExactly(
        ColoredLabel.ColoredText(0, "12"),
        ColoredLabel.ColoredText(1,"34",Color.RED),
        ColoredLabel.ColoredText(2, "56"),
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
        ColoredLabel.ColoredText(0,"This" , Color.BLUE),
        ColoredLabel.ColoredText(1," "),
        ColoredLabel.ColoredText(2,"is a " , Color.RED),
        ColoredLabel.ColoredText(3,"sample",Color.RED.interpolate(Color.GREEN, 0.5)),
        ColoredLabel.ColoredText(4," t"),
        ColoredLabel.ColoredText(5,"ext." , Color.YELLOW)
      )
  }
}