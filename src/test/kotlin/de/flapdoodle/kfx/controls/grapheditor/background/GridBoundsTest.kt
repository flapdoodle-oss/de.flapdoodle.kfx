package de.flapdoodle.kfx.controls.grapheditor.background

import javafx.geometry.BoundingBox
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GridBoundsTest {

  @Test
  fun sampleGrid() {
    val gridBounds = GridBounds.of(BoundingBox(-11.0, 13.2, 22.0, 9.3), 10.0)

    assertThat(gridBounds)
      .isEqualTo(GridBounds(-20.0, 10.0, 20.0, 30.0, 10.0))
  }
}