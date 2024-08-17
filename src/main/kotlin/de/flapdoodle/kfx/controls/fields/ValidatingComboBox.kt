package de.flapdoodle.kfx.controls.fields

import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import java.util.function.Predicate

class ValidatingComboBox<T : Any>(
  val values: List<T?>,
  val default: T?,
  val initialConverter: StringConverter<T>,
  val validate: (T?) -> String?
) : ComboBox<T>()/*, ValidatingField<T>*/ {

  private val all = FXCollections.observableArrayList(values)
  private val filtered = FilteredList(all)
  private var state: State<T> = Inactive()

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
      state = state.onEvent(UIChange.SelectionChange(newValue)).apply(this)
    }

    showingProperty().addListener { _, _, isShowing ->
      state = state.onEvent(if (isShowing) UIChange.MenuShowing() else UIChange.MenuHiding()).apply(this)
//      println("showing: $isShowing")
//      if (!isShowing) {
//        println("selection: ${selectionModel.selectedItem}")
//        filtered.predicate = null
//      }
    }

    editor.focusedProperty().addListener { _, _, newValue ->
      state = state.onEvent(if (newValue) UIChange.Focused() else UIChange.Blurred()).apply(this)
    }

    editor.textProperty().addListener { _, _, newValue ->
      state = state.onEvent(UIChange.TextChanged(newValue)).apply(this)
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
    fun onEvent(event: UIChange<T>): StateChange<T>
  }

  private fun interface Action<T: Any> {
    fun onAction(node: ValidatingComboBox<T>)
  }

  private data class StateChange<T: Any>(
    val next: State<T>,
    val actions: List<Action<T>> = emptyList()
  ) {
    constructor(next: State<T>, vararg actions: Action<T>) : this(next, listOf(*actions))

    fun apply(node: ValidatingComboBox<T>): State<T> {
      println("apply actions: $actions")
      actions.forEach { it.onAction(node) }
      println("next: $next")
      return next
    }
  }

  private sealed class UIChange<T: Any> {
    class Focused<T: Any> : UIChange<T>()
    class Blurred<T: Any> : UIChange<T>()
    data class TextChanged<T: Any>(val text: String) : UIChange<T>()
    class MenuShowing<T: Any>() : UIChange<T>()
    class MenuHiding<T: Any>() : UIChange<T>()
    data class SelectionChange<T: Any>(val selection: T?): UIChange<T>()
  }

  private class Inactive<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): StateChange<T> {
      println("${this.javaClass.simpleName}: $event")
      
      return when (event) {
        is UIChange.Focused -> StateChange(Focused())
        else -> StateChange(this)
      }
    }

  }

  private class Focused<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): StateChange<T> {
      println("${this.javaClass.simpleName}: $event")

      return when (event) {
        is UIChange.Blurred -> StateChange(Inactive())

        is UIChange.TextChanged -> Filtered.filter(event.text)
        else -> StateChange(this)
      }
    }
  }

  private class Filtered<T: Any> : State<T> {
    override fun onEvent(event: UIChange<T>): StateChange<T> {
      println("${this.javaClass.simpleName}: $event")
      return StateChange(this)
    }

    companion object {
      fun <T: Any> filter(text: String): StateChange<T> {
        return StateChange(Filtered(), SetFilter(text), ShowMenu(true))
      }
    }
  }

  private data class SetFilter<T: Any>(val text: String): Action<T> {
    override fun onAction(node: ValidatingComboBox<T>) {
      node.filtered.predicate = Predicate { true }
    }
  }

  private data class ShowMenu<T: Any>(val show: Boolean): Action<T> {
    override fun onAction(node: ValidatingComboBox<T>) {
      if (show) node.show() else node.hide()
    }
  }
}