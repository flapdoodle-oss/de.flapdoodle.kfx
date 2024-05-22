package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.controls.labels.ColoredLabel
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.extensions.bindCss
import de.flapdoodle.kfx.extensions.cssClassName
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.Border
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Text

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
    isManaged = false
//    borderProperty().bind(delegate.borderProperty())
  }

  var tooltip: Tooltip?
    set(value) { delegate.tooltip = value }
    get() { return delegate.tooltip }

  init {
    bindCss("colored-text-field")

    colors.bind(ObjectBindings.merge(valueProperty(), delegate.textProperty()) { v, t ->
      mapColors(v, t ?: "")
    })

    setAlignment(delegate, Pos.TOP_LEFT)
    setAlignment(coloredLabel, Pos.CENTER)

    children.addAll(delegate, coloredLabel)

    coloredLabel.isFocusTraversable = false
    coloredLabel.isMouseTransparent = true
    coloredLabel.opacity = 0.9


    // HACK
    delegate.skinProperty().addListener { _, _, s ->
      val f: Node? = delegate.childrenUnmodifiable.firstOrNull()
      if (f!=null && f is Parent) {
        val t = f.childrenUnmodifiable.find { it is Text } as Text?
        println("--> $t")
        if (t != null) {
//          coloredLabel.layoutYProperty().bind(t.layoutYProperty())
//          coloredLabel.translateXProperty().bind(t.layoutXProperty())
          val bbox = ObjectBindings.merge(f.boundsInParentProperty(), t.boundsInParentProperty()) { pb, tb ->
            BoundingBox(tb.minX + pb.minX, tb.minY + pb.minY, tb.width, tb.height)
          }
          bbox.addListener { _, _, n ->
            coloredLabel.resizeRelocate(n.minX, n.minY, n.width, n.height)
          }
//          t.boundsInParentProperty().addListener { _, _, n ->
//            coloredLabel.resizeRelocate(n.minX, n.minY, n.width, n.height)
//          }
//          t.layoutBoundsProperty().addListener { observable, oldValue, newValue ->
//
////            coloredLabel.resizeRelocate(newValue.minX, newValue.minY, newValue.width, newValue.height)
//            coloredLabel.resize(newValue.width, newValue.height)
//          }
//          coloredLabel.layoutYProperty().bind(t.layoutYProperty())
        }
      }
    }
  }

  override fun get() = delegate.get()
  override fun hasError() = delegate.hasError()
  override fun errorMessage() = delegate.errorMessage()
  override fun lastErrorProperty() = delegate.lastErrorProperty()
  override fun valueProperty() = delegate.valueProperty()
}