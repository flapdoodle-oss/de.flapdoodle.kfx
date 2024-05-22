package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Border
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color

class ValidatingColoredTextField<T: Any>(
  val converter: ValidatingConverter<T>,
  val default: T? = null,
  val mapException: (Exception) -> String = { it.localizedMessage },
  val mapColors: (T?, String?) -> List<ColoredLabel.Part> = { value, converted -> emptyList() },
  val onError: (TextField, String?) -> Unit = { textfield, error ->
    if (error != null) {
      textfield.tooltip = Tooltips.tooltip(error)
      textfield.border = Border.stroke(Color.RED)
    } else {
      textfield.tooltip = null
      textfield.border = null
    }
  }
): StackPane(), ValidatingField<T> {
  
  private val delegate = ValidatingTextField(converter, default, mapException, onError)

  private val colors = SimpleObjectProperty<List<ColoredLabel.Part>>(emptyList())
  private val coloredLabel = ColoredLabel(delegate.textProperty(), colors).apply {
    cssClassName("colored-label")
    borderProperty().bind(delegate.borderProperty())
  }

  var tooltip: Tooltip?
    set(value) { delegate.tooltip = value }
    get() { return delegate.tooltip }

  init {
    bindCss("colored-text-field")

    colors.bind(ObjectBindings.map(valueProperty()) { v ->
      val colors = mapColors(v, v?.let(converter::toString) ?: "")
      println("colors: $colors")
      colors
    })

    children.addAll(coloredLabel, delegate)

    delegate.focusedProperty().addListener { observable, oldValue, newValue ->
      if (!newValue) {
        delegate.opacity = 0.0
        coloredLabel.opacity = 1.0
      } else {
        delegate.opacity = 1.0
        coloredLabel.opacity = 0.0
      }
    }

    coloredLabel.isFocusTraversable = true
    coloredLabel.focusedProperty().addListener { observable, oldValue, newValue ->
      if (newValue) {
        delegate.requestFocus()
      }
    }
    coloredLabel.addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
      delegate.requestFocus()
    }
  }

  override fun get() = delegate.get()
  override fun hasError() = delegate.hasError()
  override fun errorMessage() = delegate.errorMessage()
  override fun lastErrorProperty() = delegate.lastErrorProperty()
  override fun valueProperty() = delegate.valueProperty()
}