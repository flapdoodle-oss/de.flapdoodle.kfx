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

import javafx.collections.FXCollections
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MappingListChangeListenerTest {

  abstract class Case {
    val source = FXCollections.observableArrayList<String>()
    val destination = FXCollections.observableArrayList<String>()
    val testee = MappingListChangeListener<String, String>(destination) { ">$it<" }

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
        .containsExactly(">One<")
    }

    @Test
    fun addMoreThanOne() {
      source.addAll("1", "2", "3")
      assertThat(destination)
        .hasSize(3)
        .containsExactly(">1<", ">2<", ">3<")
    }

    @Test
    fun addSomethingInBetween() {
      source.addAll("1", "2", "3")
      source.addAll(2, listOf("a", "b"))
      assertThat(destination)
        .hasSize(5)
        .containsExactly(">1<", ">2<", ">a<", ">b<", ">3<")
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
        .containsExactly(">3<")
    }

    @Test
    fun removeMoreThanOneInBetween() {
      source.addAll("1", "2", "3", "4")
      source.removeAll("2", "3")
      assertThat(destination)
        .hasSize(2)
        .containsExactly(">1<", ">4<")
    }

    @Test
    fun removeMoreThanOneAtTheEnd() {
      source.addAll("1", "2", "3")
      source.removeAll("2", "3")
      assertThat(destination)
        .hasSize(1)
        .containsExactly(">1<")
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
        .containsExactly(">1<", ">a<", ">3<")
    }

    @Test
    fun replaceOneWithTwo() {
      source.addAll("1")
      source.setAll("1", "2")
      assertThat(destination)
        .hasSize(2)
        .containsExactly(">1<", ">2<")
    }

    @Test
    fun replaceTwoWithOne() {
      source.addAll("1","2")
      source.setAll("1")
      assertThat(destination)
        .hasSize(1)
        .containsExactly(">1<")
    }
  }

  @Nested
  inner class Permutate : Case() {
    @Test
    fun permutateList() {
      source.addAll("3","1","2")
      source.sort()
      assertThat(destination)
        .hasSize(3)
        .containsExactly(">1<", ">2<", ">3<")
    }
  }
}