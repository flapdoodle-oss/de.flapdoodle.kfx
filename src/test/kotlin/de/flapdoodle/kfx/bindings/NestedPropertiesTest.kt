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

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.Button
import javafx.scene.effect.BlendMode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class NestedPropertiesTest {
  @Test
  fun updatePropertyWhenNodeWrapperIsSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty(button)

    val testee = NestedProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }
    var changes = emptyList<String?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isEqualTo("b: null, w: 0.0")
    assertThat(changes).isEmpty()

    button.blendMode = BlendMode.BLUE
    button.resize(200.0, 100.0)

    assertThat(testee.value)
      .isEqualTo("b: BLUE, w: 200.0")
    assertThat(changes)
      .hasSize(2)
    assertThat(changes)
      .containsExactly("b: BLUE, w: 0.0", "b: BLUE, w: 200.0")

    button.blendMode = null

    assertThat(testee.value).isEqualTo("b: null, w: 200.0")
    assertThat(changes)
      .hasSize(3)
    assertThat(changes)
      .containsExactly("b: BLUE, w: 0.0", "b: BLUE, w: 200.0", "b: null, w: 200.0")
  }

  @Test
  fun updatePropertyWhenNodeWrapperIsNotSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty<Button>(null)

    val testee = NestedProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }
    var changes = emptyList<String?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isNull()
    assertThat(changes).isEmpty()

    button.blendMode = BlendMode.BLUE
    button.resize(200.0, 100.0)

    nodeWrapper.value = button

    assertThat(testee.value)
      .isEqualTo("b: BLUE, w: 200.0")
    assertThat(changes)
      .hasSize(1)
    assertThat(changes)
      .containsExactly("b: BLUE, w: 200.0")

    nodeWrapper.value = null

    assertThat(testee.value).isNull()
    assertThat(changes)
      .hasSize(2)
    assertThat(changes)
      .containsExactly("b: BLUE, w: 200.0", null)
  }

  @Test
  fun listenerDelegate() {
    val nodeWrapper = SimpleObjectProperty(Button().apply {
      blendMode = BlendMode.BLUE
    })

    val testee = NestedProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }

    var changes = emptyList<String?>()
    var invalidateCalled = false

    val changeListener = ChangeListener<String?> { _, _, change ->
      changes = changes + change
    }
    val invalidationListener = InvalidationListener {
      invalidateCalled = true
    }

    testee.addListener(changeListener)
    testee.addListener(invalidationListener)

    nodeWrapper.value.blendMode = BlendMode.RED

    assertThat(changes)
      .hasSize(1)
      .containsExactly("b: RED, w: 0.0")
    assertThat(invalidateCalled).isTrue()

    testee.removeListener(changeListener)
    testee.removeListener(invalidationListener)

    invalidateCalled = false
    nodeWrapper.value.blendMode = BlendMode.BLUE

    assertThat(changes)
      .hasSize(1)
      .containsExactly("b: RED, w: 0.0")
    assertThat(invalidateCalled).isFalse()
  }

}