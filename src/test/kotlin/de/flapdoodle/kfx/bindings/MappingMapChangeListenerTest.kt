package de.flapdoodle.kfx.bindings

import javafx.collections.FXCollections
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MappingMapChangeListenerTest {
  abstract class Case {
    val source = FXCollections.observableHashMap<String, Int>()
    val destination = FXCollections.observableHashMap<String, Int>()
    val testee = MappingMapChangeListener<String, Int, Int>(destination) { it + 1 }

    init {
      source.addListener(testee)
    }

  }

  @Nested
  inner class Add : Case() {
    @Test
    fun addOne() {
      source.put("One", 3)
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry("One", 4)
    }

    @Test
    fun addMoreThanOne() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      Assertions.assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf("1" to 2, "2" to 3, "3" to 4)
        )
    }

    @Test
    fun addSomethingInBetween() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      source.putAll(mapOf("a" to 10, "b" to 11))
      Assertions.assertThat(destination)
        .hasSize(5)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf("1" to 2, "2" to 3, "a" to 11, "b" to 12, "3" to 4)
        )
    }
  }

  @Nested
  inner class Remove : Case() {
    @Test
    fun removeOne() {
      source.put("One", 1)
      source.remove("One")
      Assertions.assertThat(destination)
        .hasSize(0)
    }

    @Test
    fun removeMoreThanOneAtStart() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      source.remove("1")
      source.remove("2")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry("3",4)
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3, "4" to 4))
      source.remove("2")
      source.remove("3")
      Assertions.assertThat(destination)
        .hasSize(2)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf("1" to 2, "4" to 5)
        )
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      source.remove("2")
      source.remove( "3")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry("1" ,2 )
    }

    @Test
    fun removeAll() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      source.clear()
      Assertions.assertThat(destination)
        .hasSize(0)
    }
  }

  @Nested
  inner class Replace : Case() {
    @Test
    fun replaceOne() {
      source.putAll(mapOf("1" to 1, "2" to 2, "3" to 3))
      source.put("1", 2)
      Assertions.assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf("1" to 3, "2" to 3, "3" to 4)
        )
    }
  }
}