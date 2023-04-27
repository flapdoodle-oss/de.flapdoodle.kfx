package de.flapdoodle.kfx.bindings

import javafx.collections.FXCollections
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MapKVListChangeListenerTest {

  abstract class Case {
    val source = FXCollections.observableArrayList<String>()
    val destination = FXCollections.observableHashMap<String, Int>()
    val testee = MapKVListChangeListener<String, String, Int>(destination, { ">$it<" }) { it.length }

    init {
      source.addListener(testee)
    }

  }

  @Nested
  inner class Add : Case() {
    @Test
    fun addOne() {
      source.add("One")
      assertThat(destination)
        .hasSize(1)
        .containsEntry(">One<", 3)
    }

    @Test
    fun addMoreThanOne() {
      source.addAll("1", "2", "3")
      assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">2<" to 1, ">3<" to 1)
        )
    }

    @Test
    fun addSomethingInBetween() {
      source.addAll("1", "2", "3")
      source.addAll(2, listOf("a", "b"))
      assertThat(destination)
        .hasSize(5)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">2<" to 1, ">a<" to 1, ">b<" to 1, ">3<" to 1)
        )
    }
  }

  @Nested
  inner class Remove : Case() {
    @Test
    fun removeOne() {
      source.add("One")
      source.remove("One")
      assertThat(destination)
        .hasSize(0)
    }

    @Test
    fun removeMoreThanOneAtStart() {
      source.addAll("1", "2", "3")
      source.removeAll("1", "2")
      assertThat(destination)
        .hasSize(1)
        .containsEntry(">3<",1)
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.addAll("1", "2", "3", "4")
      source.removeAll("2", "3")
      assertThat(destination)
        .hasSize(2)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">4<" to 1)
        )
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.addAll("1", "2", "3")
      source.removeAll("2", "3")
      assertThat(destination)
        .hasSize(1)
        .containsEntry(">1<" ,1 )
    }

    @Test
    fun removeAll() {
      source.addAll("1", "2", "3")
      source.clear()
      assertThat(destination)
        .hasSize(0)
    }
  }

  @Nested
  inner class Replace : Case() {
    @Test
    fun replaceOne() {
      source.addAll("1", "2", "3")
      source.set(1, "a")
      assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">a<" to 1, ">3<" to 1)
        )
    }
  }

  @Nested
  inner class Permutate : Case() {
    @Test
    fun permutateList() {
      source.addAll("3", "1", "2")
      source.sort()
      assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">2<" to 1, ">3<" to 1)
        )
    }
  }
}