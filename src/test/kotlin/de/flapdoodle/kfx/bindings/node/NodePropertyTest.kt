package de.flapdoodle.kfx.bindings.node

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Button
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class NodePropertyTest {

  @Test
  fun updatePropertyWhenNodeWrapperIsSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty(button)

    val testee = NodeProperty(nodeWrapper, Button::backgroundProperty)
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

    val testee = NodeProperty(nodeWrapper, Button::backgroundProperty)
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
    val testee = NodeProperty(nodeWrapper, Button::backgroundProperty)

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