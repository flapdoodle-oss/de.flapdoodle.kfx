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
package de.flapdoodle.kfx.bindings.list

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class List2ObservableListChangeListenerTest {
  abstract class Case {
    val source = SimpleObjectProperty(emptyList<String>())
    val destination = FXCollections.observableArrayList<String>()
    val testee = List2ObservableListChangeListener<String, String>(destination) { ">$it<" }

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
        .containsExactly(">One<")
    }

    @Test
    fun addMoreThanOne() {
      source.value = source.value + listOf("1", "2", "3")
      Assertions.assertThat(destination)
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
      Assertions.assertThat(destination)
        .hasSize(0)
    }

    @Test
    fun removeMoreThanOneAtStart() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = source.value - listOf("1", "2")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsExactly(">3<")
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.value = source.value + listOf("1", "2", "3", "4")
      source.value = source.value - listOf("2", "3")
      Assertions.assertThat(destination)
        .hasSize(2)
        .containsExactly(">1<", ">4<")
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.value = source.value + listOf("1", "2", "3")
      source.value = source.value - listOf("2", "3")
      Assertions.assertThat(destination)
        .hasSize(1)
        .containsExactly(">1<")
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
        .containsExactly(">1<", ">a<", ">3<")
    }
  }

  @Nested
  inner class Permutate : Case() {
    @Test
    fun permutateList() {
      source.value = listOf("3","1","2")
      source.value = listOf("1", "2", "3")
      Assertions.assertThat(destination)
        .hasSize(3)
        .containsExactly(">1<", ">2<", ">3<")
    }
  }
}