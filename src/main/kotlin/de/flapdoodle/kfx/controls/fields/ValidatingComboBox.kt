package de.flapdoodle.kfx.controls.fields

import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ComboBox
import javafx.util.StringConverter

class ValidatingComboBox<T : Any>(
  val values: List<T?>,
  val default: T?,
  val initialConverter: StringConverter<T>,
  val validate: (T?) -> String?
) : ComboBox<T>()/*, ValidatingField<T>*/ {

  private val all = FXCollections.observableArrayList(values)
  private val filtered = FilteredList(all)
  private var state: State<T> = Start()

  init {
    isEditable = true
    items = filtered
    converter = object : StringConverter<T>() {
      override fun toString(value: T?): String {
        return initialConverter.toString(value)
      }

      override fun fromString(value: String?): T? {
        return if (filtered.size==1) {
          filtered[0]
        } else {
          selectionModel.selectedItem
        }
//        return selectionModel.selectedItem
      }

    }

    selectionModel.selectedItemProperty().addListener { _, _, newValue ->
      state = state.onEvent(UIChange.SelectionChange(newValue))
    }

    showingProperty().addListener { _, _, isShowing ->
      state = state.onEvent(if (isShowing) UIChange.MenuShowing() else UIChange.MenuHiding())
//      println("showing: $isShowing")
//      if (!isShowing) {
//        println("selection: ${selectionModel.selectedItem}")
//        filtered.predicate = null
//      }
    }

    editor.focusedProperty().addListener { _, _, newValue ->
      state = state.onEvent(if (newValue) UIChange.EditorFocused() else UIChange.EditorBlured())
    }

    editor.textProperty().addListener { _, _, newValue ->
      state = state.onEvent(UIChange.TextChanged(newValue))
//      println("text: '$newValue'")
//      if (selectionModel.selectedItem==null && newValue != null && newValue.isNotBlank()) {
//        println("filter--> '$newValue'")
//        filtered.predicate = Predicate { item -> initialConverter.toString(item).contains(newValue) }
//        if (!isShowing) {
//          show()
//        }
//      }
    }
  }

  private interface State<T: Any> {
    fun onEvent(event: UIChange<T>): State<T>
  }

  private sealed class UIChange<T: Any> {
    class EditorFocused<T: Any> : UIChange<T>()
    class EditorBlured<T: Any> : UIChange<T>()
    data class TextChanged<T: Any>(val text: String) : UIChange<T>()
    class MenuShowing<T: Any>() : UIChange<T>()
    class MenuHiding<T: Any>() : UIChange<T>()
    data class SelectionChange<T: Any>(val selection: T?): UIChange<T>()
  }

  private class Start<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): State<T> {
      println("${this.javaClass.simpleName}: $event")
      
      return when (event) {
        is UIChange.EditorFocused -> Focused()
        else -> this
      }
    }

  }

  private class Focused<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): State<T> {
      println("${this.javaClass.simpleName}: $event")

      return when (event) {
        is UIChange.EditorBlured -> Start()
        else -> this
      }
    }
  }

  private class ListOpened<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): State<T> {
      println("${this.javaClass.simpleName}: $event")
      return when (event) {
        else -> this
      }
    }

  }
}