package de.flapdoodle.kfx.bindings.node

import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class ChildNodePropertyTest {

  @Test
  fun updatePropertyWhenNodeWrapperIsSet() {
    val button = Button("X")
    val pane = Pane().apply {
      children.add(button)
    }
    val paneWrapper = SimpleObjectProperty(pane)

    val testee = ChildNodeProperty(paneWrapper, ChildNodeFilter.isInstance(Button::class))
    var changes = emptyList<Button?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value)
      .isEqualTo(button)
    assertThat(changes).isEmpty()

    pane.children.clear()

    assertThat(testee.value).isNull()
    assertThat(changes)
      .hasSize(1)
      .containsExactly(null)

    pane.children.addAll(Label("ignore"), button, TextField("nope"))

    assertThat(testee.value).isEqualTo(button)
    assertThat(changes)
      .hasSize(2)
      .containsExactly(null, button)
  }


  @Test
  fun updatePropertyWhenNodeWrapperIsNotSet() {
    val button = Button("X")
    val pane = Pane().apply {
      children.addAll(Label("ignore"), button, TextField("nope"))
    }
    val paneWrapper = SimpleObjectProperty<Pane>(null)

    val testee = ChildNodeProperty(paneWrapper, ChildNodeFilter.isInstance(Button::class))
    var changes = emptyList<Button?>()
    testee.addListener { _, _, change ->
      changes = changes + change
    }

    assertThat(testee.value).isNull()
    assertThat(changes).isEmpty()

    paneWrapper.value = pane

    assertThat(testee.value).isEqualTo(button)
    assertThat(changes)
      .hasSize(1)
      .containsExactly(button)

    paneWrapper.value = null

    assertThat(testee.value).isNull()
    assertThat(changes)
      .hasSize(2)
      .containsExactly(button, null)
  }

  @Test
  fun listenerDelegate() {
    val button = Button("X")
    val pane = Pane()
    val paneWrapper = SimpleObjectProperty<Pane>(pane)

    val testee = ChildNodeProperty(paneWrapper, ChildNodeFilter.isInstance(Button::class))
    var changes = emptyList<Button?>()
    var invalidateCalled = false

    val changeListener = ChangeListener<Button?> { _, _, change ->
      changes = changes + change
    }
    val invalidationListener = InvalidationListener {
      invalidateCalled = true
    }

    testee.addListener(changeListener)
    testee.addListener(invalidationListener)

    paneWrapper.value.children.addAll(Label("ignore"), button, TextField("nope"))

    assertThat(changes)
      .hasSize(1)
      .containsExactly(button)
    assertThat(invalidateCalled).isTrue()

    testee.removeListener(changeListener)
    testee.removeListener(invalidationListener)

    invalidateCalled = false
    paneWrapper.value.children.clear()

    assertThat(changes)
      .hasSize(1)
      .containsExactly(button)
    assertThat(invalidateCalled).isFalse()
  }

}