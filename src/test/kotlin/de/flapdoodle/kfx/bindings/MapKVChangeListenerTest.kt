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
package de.flapdoodle.kfx.bindings

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MapKVChangeListenerTest {

  abstract class Case {
    val source = SimpleObjectProperty(emptyList<String>())
    val destination = FXCollections.observableHashMap<String, Int>()
    val testee = MapKVChangeListener<String, String, Int>(destination, { ">$it<" }) { it.length }

    init {
      source.addListener(testee)
    }

  }

  @Nested
  inner class Add : Case() {
    @Test
    fun addOne() {
      source.value = source.value + "One"
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry(">One<", 3)
    }

    @Test
    fun addMoreThanOne() {
      source.value = source.value + listOf("1", "2", "3")
      Assertions.assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">2<" to 1, ">3<" to 1)
        )
    }

    @Test
    fun addSomethingInBetween() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = listOf("1", "2") + listOf("a", "b") + "3"
      Assertions.assertThat(destination)
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
      source.value = source.value + "One"
      source.value = emptyList()
      Assertions.assertThat(destination)
        .hasSize(0)
    }

    @Test
    fun removeMoreThanOneAtStart() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = listOf("3")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry(">3<",1)
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.value = source.value + listOf("1", "2", "3", "4")
      source.value = listOf("1", "4")
      Assertions.assertThat(destination)
        .hasSize(2)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">4<" to 1)
        )
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = listOf("1")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsEntry(">1<" ,1 )
    }

    @Test
    fun removeAll() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = emptyList()
      Assertions.assertThat(destination)
        .hasSize(0)
    }
  }

  @Nested
  inner class Replace : Case() {
    @Test
    fun replaceOne() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = listOf("1", "a", "3")
      Assertions.assertThat(destination)
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
      source.value = source.value + listOf("3", "1", "2")
      source.value = listOf("1", "2", "3")
      Assertions.assertThat(destination)
        .hasSize(3)
        .containsExactlyInAnyOrderEntriesOf(
          mapOf(">1<" to 1, ">2<" to 1, ">3<" to 1)
        )
    }
  }
}
