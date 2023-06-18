package de.flapdoodle.kfx.extensions

import de.flapdoodle.kfx.extensions.Point2DMath.CardinalDirections.cardinalDirection
import de.flapdoodle.kfx.extensions.Point2DMath.angle
import de.flapdoodle.kfx.extensions.Point2DMath.cardinalDirection
import de.flapdoodle.kfx.types.CardinalDirection
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class Point2DMathTest {
  @Test
  fun angle() {
    val center = randomPoint()
    val delta = ThreadLocalRandom.current().nextDouble(1.0, 10.0)

    assertThat(angle(center, center.add(delta, 0.0))).isCloseTo(0.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(delta, delta))).isCloseTo(45.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(0.0, delta))).isCloseTo(90.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, delta))).isCloseTo(135.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, 0.0))).isCloseTo(180.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(-delta, -delta))).isCloseTo(-135.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(0.0, -delta))).isCloseTo(-90.0, Percentage.withPercentage(99.0))
    assertThat(angle(center, center.add(delta, -delta))).isCloseTo(-45.0, Percentage.withPercentage(99.0))
  }

  @Test
  fun cardinalDirection() {
    assertThat(cardinalDirection(0.0)).isEqualTo(CardinalDirection.EAST)
    assertThat(cardinalDirection(45.0)).isEqualTo(CardinalDirection.NORTHEAST)
    assertThat(cardinalDirection(90.0)).isEqualTo(CardinalDirection.NORTH)
    assertThat(cardinalDirection(135.0)).isEqualTo(CardinalDirection.NORTHWEST)
    assertThat(cardinalDirection(180.0)).isEqualTo(CardinalDirection.WEST)
    assertThat(cardinalDirection(-45.0)).isEqualTo(CardinalDirection.SOUTHEAST)
    assertThat(cardinalDirection(-90.0)).isEqualTo(CardinalDirection.SOUTH)
    assertThat(cardinalDirection(-135.0)).isEqualTo(CardinalDirection.SOUTHWEST)
    assertThat(cardinalDirection(-180.0)).isEqualTo(CardinalDirection.WEST)

    assertThat(cardinalDirection(-179.0)).isEqualTo(CardinalDirection.WEST)
    assertThat(cardinalDirection(179.0)).isEqualTo(CardinalDirection.WEST)

    assertThat(cardinalDirection(179.0+(10*360.0))).isEqualTo(CardinalDirection.WEST)
  }

  fun randomPoint(): Point2D {
    val current = ThreadLocalRandom.current()
    return Point2D(current.nextDouble(-100.0, 100.0), current.nextDouble(-100.0, 100.0))
  }
}