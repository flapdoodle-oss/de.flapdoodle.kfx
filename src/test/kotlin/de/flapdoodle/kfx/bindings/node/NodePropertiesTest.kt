package de.flapdoodle.kfx.bindings.node

import de.flapdoodle.kfx.bindings.ObjectBindings
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
class NodePropertiesTest {
  @Test
  fun updatePropertyWhenNodeWrapperIsSet() {
    val button = Button()
    val nodeWrapper = SimpleObjectProperty(button)

    val testee = NodeProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }
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

    val testee = NodeProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }
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

    val testee = NodeProperties(nodeWrapper) { ObjectBindings.merge(it.blendModeProperty(), it.widthProperty()) { b, w -> "b: $b, w: $w" } }

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