package de.flapdoodle.kfx.bindings.list

import javafx.beans.property.SimpleObjectProperty
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class List2ListChangeListenerTest {
  abstract class Case {
    val source = SimpleObjectProperty(emptyList<String>())
    val destination = SimpleObjectProperty(emptyList<String>())
    val testee = List2ListChangeListener<String, String>(destination) { ">$it<" }

    init {
      source.addListener(testee)
    }
  }

  @Nested
  inner class Add : Case() {
    @Test
    fun addOne() {
      source.value = source.value + "One"
      Assertions.assertThat(destination.value)
        .hasSize(1)
        .containsExactly(">One<")
    }

    @Test
    fun addMoreThanOne() {
      source.value = source.value + listOf("1", "2", "3")
      Assertions.assertThat(destination.value)
        .hasSize(3)
        .containsExactly(">1<", ">2<", ">3<")
    }
  }

  @Nested
  inner class Remove : Case() {
    @Test
    fun removeOne() {
      source.value = source.value + "One"
      source.value = source.value - "One"
      Assertions.assertThat(destination.value)
        .hasSize(0)
    }

    @Test
    fun removeMoreThanOneAtStart() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = source.value - listOf("1", "2")
      Assertions.assertThat(destination.value)
        .hasSize(1)
        .containsExactly(">3<")
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.value = source.value + listOf("1", "2", "3", "4")
      source.value = source.value - listOf("2", "3")
      Assertions.assertThat(destination.value)
        .hasSize(2)
        .containsExactly(">1<", ">4<")
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = source.value - listOf("2", "3")
      Assertions.assertThat(destination.value)
        .hasSize(1)
        .containsExactly(">1<")
    }

    @Test
    fun removeAll() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = emptyList()
      Assertions.assertThat(destination.value)
        .hasSize(0)
    }
  }

  @Nested
  inner class Replace : Case() {
    @Test
    fun replaceOne() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = listOf("1", "a", "3")
      Assertions.assertThat(destination.value)
        .hasSize(3)
        .containsExactly(">1<", ">a<", ">3<")
    }
  }

  @Nested
  inner class Permutate : Case() {
    @Test
    fun permutateList() {
      source.value = listOf("3","1","2")
      source.value = listOf("1", "2", "3")
      Assertions.assertThat(destination.value)
        .hasSize(3)
        .containsExactly(">1<", ">2<", ">3<")
    }
  }
}