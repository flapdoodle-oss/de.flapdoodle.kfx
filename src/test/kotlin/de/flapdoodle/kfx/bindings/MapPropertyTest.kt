package de.flapdoodle.kfx.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.geometry.Point2D
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapPropertyTest {

  @Test
  fun setKeyShouldSetPropertyValue() {
    val map = FXCollections.observableHashMap<String, ObservableValue<Point2D>>()
    val testee = MapProperty(map, "foo")
    var changes = emptyList<Point2D?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isNull()
    assertThat(changes).isEmpty()

    val firstPointProperty = SimpleObjectProperty(Point2D(1.0, 2.0))
    
    map["foo"] = firstPointProperty

    assertThat(testee.value).isEqualTo(Point2D(1.0, 2.0))
    assertThat(changes).containsExactly(Point2D(1.0, 2.0))

    firstPointProperty.value = Point2D(2.0, 3.0)

    assertThat(testee.value).isEqualTo(Point2D(2.0, 3.0))
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0))

    val secondPointProperty = SimpleObjectProperty(Point2D(0.0, 0.0))

    map["foo"] = secondPointProperty

    assertThat(testee.value).isEqualTo(Point2D(0.0, 0.0))
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0), Point2D(0.0, 0.0))

    firstPointProperty.value = Point2D(123.0, 456.0)

    assertThat(testee.value).isEqualTo(Point2D(0.0, 0.0))
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0), Point2D(0.0, 0.0))

    secondPointProperty.value = null

    assertThat(testee.value).isNull()
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0), Point2D(0.0, 0.0), null)

    secondPointProperty.value = Point2D(10.0, 10.0)

    assertThat(testee.value).isEqualTo(Point2D(10.0, 10.0))
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0), Point2D(0.0, 0.0), null, Point2D(10.0, 10.0))

    map.remove("foo")

    assertThat(testee.value).isNull()
    assertThat(changes).containsExactly(Point2D(1.0, 2.0), Point2D(2.0, 3.0), Point2D(0.0, 0.0), null, Point2D(10.0, 10.0), null)
  }
}