package de.flapdoodle.kfx.controls.fields

import de.flapdoodle.kfx.controls.Tooltips
import de.flapdoodle.kfx.converters.ValidatingConverter
import de.flapdoodle.kfx.converters.impl.SomethingLeftException
import de.flapdoodle.kfx.converters.impl.TemporalAccessorParseException
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.DatePicker
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Border
import javafx.scene.paint.Color
import java.time.LocalDate
import java.time.format.DateTimeParseException

class ValidatingDatePicker(
  converter: ValidatingConverter<LocalDate>,
  val mapException: (Exception) -> String = { it.localizedMessage },
  val onError: (ValidatingDatePicker, String?) -> Unit = { textfield, error ->
    if (error != null) {
      textfield.tooltip = Tooltips.tooltip(error)
      textfield.border = Border.stroke(Color.RED)
    } else {
      textfield.tooltip = null
      textfield.border = null
    }
  }
) : DatePicker() {
  private val lastError = SimpleObjectProperty<String>(null)

  init {
    this.converter = converter.asStringConverter {
//      lastTextBeforeError.value = editor.text
      lastError.value = if (it!=null) mapException(it) else null
      rethrowAsDateEx(it)
    }
    this.value = value

    lastError.addListener { _, _, error ->
      onError(this, error)
    }
  }

  private fun rethrowAsDateEx(ex: Exception?) {
    if (ex!=null) {
      throw when (ex) {
        is SomethingLeftException -> DateTimeParseException(ex.localizedMessage,ex.input(), ex.errorIndex(), ex)
        is TemporalAccessorParseException -> DateTimeParseException(ex.localizedMessage,ex.input(), ex.errorOffset, ex)
        else -> RuntimeException("could not map ex", ex)
      }
    }
  }

  fun set(v: LocalDate?) {
    valueProperty().value = v
  }

  fun get(): LocalDate? {
    return valueProperty().value
  }

  fun hasError(): Boolean {
    return lastError.value != null
  }

  fun setErrorMessage(message: String?) {
    lastError.value = message
  }

  fun errorMessage() = lastError.value
}