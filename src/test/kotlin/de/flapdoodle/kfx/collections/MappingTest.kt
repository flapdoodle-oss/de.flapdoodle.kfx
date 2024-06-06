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
package de.flapdoodle.kfx.collections

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class MappingTest {
  @Test
  fun addToEmpty() {
    val testee = Mapping<String, Int, String>()

    testee.add("Foo",1,"Foo=1")

    assertThat(testee["Foo"]).isEqualTo("Foo=1")
    assertThat(testee.reverseKey("Foo")).isEqualTo(1)
    assertThat(testee.key(1)).isEqualTo("Foo")

    var withValue: String? = null
    testee.with("Foo") {
      withValue = it
    }
    assertThat(withValue).isEqualTo("Foo=1")
  }

  @Test
  fun collisions() {
    val testee = Mapping<String, Int, String>()

    testee.add("Foo",1,"Foo=1")

    assertThatThrownBy { testee.add("Foo", 1, "Foo=1") }
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { testee.add("Foo", 2, "Foo=2") }
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { testee.add("Bar", 1, "Bar=1") }
      .isInstanceOf(IllegalArgumentException::class.java)

    assertThatThrownBy { testee.remove("Bar") }
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { testee.replace("Bar", "Bar=2") }
      .isInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun removeEntry() {
    val testee = Mapping<String, Int, String>()

    testee.add("Foo",1,"Foo=1")

    assertThat(testee["Foo"]).isEqualTo("Foo=1")
    assertThat(testee.reverseKey("Foo")).isEqualTo(1)
    assertThat(testee.key(1)).isEqualTo("Foo")

    var removedValue: String? = null
    testee.remove("Foo") {
      removedValue = it
    }
    assertThat(removedValue).isEqualTo("Foo=1")

    assertThat(testee["Foo"]).isNull()
    assertThat(testee.reverseKey("Foo")).isNull()
    assertThat(testee.key(1)).isNull()
  }

  @Test
  fun replaceEntry() {
    val testee = Mapping<String, Int, String>()

    testee.add("Foo",1,"Foo=1")

    assertThat(testee["Foo"]).isEqualTo("Foo=1")
    assertThat(testee.reverseKey("Foo")).isEqualTo(1)
    assertThat(testee.key(1)).isEqualTo("Foo")

    var removedValue: String? = null
    testee.replace("Foo", "Foo replaced") {
      removedValue = it
    }
    assertThat(removedValue).isEqualTo("Foo=1")

    assertThat(testee["Foo"]).isEqualTo("Foo replaced")
    assertThat(testee.reverseKey("Foo")).isEqualTo(1)
    assertThat(testee.key(1)).isEqualTo("Foo")
  }
}