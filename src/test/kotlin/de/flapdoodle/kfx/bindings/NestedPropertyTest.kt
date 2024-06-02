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
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class NestedPropertyTest {

  @Test
  fun updatePropertyWhenNodeWrapperIsSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty(button)

    val testee = NestedProperty(nodeWrapper, Button::backgroundProperty)
    var changes = emptyList<Background?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isNull()
    assertThat(changes).isEmpty()

    button.background = Background.fill(Color.BLUE)
    button.background = Background.fill(Color.RED)

    assertThat(testee.value)
      .isEqualTo(Background.fill(Color.RED))
    assertThat(changes)
      .hasSize(2)
    assertThat(changes)
      .containsExactly(Background.fill(Color.BLUE), Background.fill(Color.RED))

    button.background = null

    assertThat(testee.value).isNull()
    assertThat(changes)
      .hasSize(3)
    assertThat(changes)
      .containsExactly(Background.fill(Color.BLUE), Background.fill(Color.RED), null)
  }

  @Test
  fun updatePropertyWhenNodeWrapperIsNotSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty<Button>(null)

    val testee = NestedProperty(nodeWrapper, Button::backgroundProperty)
    var changes = emptyList<Background?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isNull()
    assertThat(changes).isEmpty()

    button.background = Background.fill(Color.BLUE)
    button.background = Background.fill(Color.RED)

    nodeWrapper.value = button

    assertThat(testee.value)
      .isEqualTo(Background.fill(Color.RED))
    assertThat(changes)
      .hasSize(1)
    assertThat(changes)
      .containsExactly(Background.fill(Color.RED))

    nodeWrapper.value = null

    assertThat(testee.value).isNull()
    assertThat(changes)
      .hasSize(2)
    assertThat(changes)
      .containsExactly(Background.fill(Color.RED), null)
  }

  @Test
  fun listenerDelegate() {
    val nodeWrapper = SimpleObjectProperty(Button().apply {
      background = Background.fill(Color.YELLOW)
    })
    val testee = NestedProperty(nodeWrapper, Button::backgroundProperty)

    var changes = emptyList<Background?>()
    var invalidateCalled = false

    val changeListener = ChangeListener<Background?> { _, _, change ->
      changes = changes + change
    }
    val invalidationListener = InvalidationListener {
      invalidateCalled = true
    }

    testee.addListener(changeListener)
    testee.addListener(invalidationListener)

    nodeWrapper.value.background = Background.fill(Color.RED)

    assertThat(changes).hasSize(1).containsExactly(Background.fill(Color.RED))
    assertThat(invalidateCalled).isTrue()

    testee.removeListener(changeListener)
    testee.removeListener(invalidationListener)

    invalidateCalled = false
    nodeWrapper.value.background = Background.fill(Color.BLUE)

    assertThat(changes).hasSize(1).containsExactly(Background.fill(Color.RED))
    assertThat(invalidateCalled).isFalse()
  }
}