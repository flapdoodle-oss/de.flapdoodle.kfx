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
package de.flapdoodle.kfx.controls.fields

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import javafx.scene.Scene
import javafx.scene.control.ComboBox
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.util.*


class ComboBoxSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {
      val box = ComboBox<String?>()
      box.isEditable = true


      // For the combo box filter to work properly we need to create the item
      // list and wrap it in a FilteredList.
      val items = FXCollections.observableArrayList(
        "One", "Two", "Three", "OneTwo", "ThreeTwo",
        "OneTwoThree"
      )
      val filteredItems = FilteredList(items)


      // Then you need to provide the InputFilter with the FilteredList in the
      // constructor call.
      box.editor.textProperty().addListener(InputFilterX(box, filteredItems, false))

      box.items = filteredItems

      val view = BorderPane()
      view.center = box

      stage.scene = Scene(view)
      stage.show()
    }
  }



  class InputFilterX
  /**
   * @param box
   * The combo box to whose textProperty this listener is
   * added.
   * @param items
   * The [FilteredList] containing the items in the list.
   */ @JvmOverloads constructor(
    private val box: ComboBox<String?>, private val items: FilteredList<String>, private val upperCase: Boolean = false, private val maxLength: Int = -1,
    private val restriction: String? = null
  ) : ChangeListener<String?> {
    override fun changed(observable: ObservableValue<out String?>?, oldValue: String?, newValue: String?) {
      val value: StringProperty = SimpleStringProperty(newValue)

      // If any item is selected we save that reference.
      val selected = if (box.selectionModel.selectedItem != null
      ) box.selectionModel.selectedItem else null

      var selectedString: String? = null
      // We save the String of the selected item.
      if (selected != null) {
        selectedString = selected
      }

      if (upperCase) {
        value.set(value.get().uppercase(Locale.getDefault()))
      }

      if (maxLength >= 0 && value.get().length > maxLength) {
        value.set(oldValue)
      }

      if (restriction != null) {
        if (!value.get().matches("$restriction*".toRegex())) {
          value.set(oldValue)
        }
      }

      // If an item is selected and the value in the editor is the same
      // as the selected item we don't filter the list.
      if (selected != null && value.get() == selectedString) {
        // This will place the caret at the end of the string when
        // something is selected.
        Platform.runLater { box.editor.end() }
      } else {
        items.setPredicate { item: String ->
          val itemString = item
          if (itemString.uppercase(Locale.getDefault()).contains(value.get().uppercase(Locale.getDefault()))) {
            return@setPredicate true
          } else {
            return@setPredicate false
          }
        }
      }

      // If the popup isn't already showing we show it.
      if (!box.isShowing) {
        // If the new value is empty we don't want to show the popup,
        // since
        // this will happen when the combo box gets manually reset.
        if (newValue?.isNotEmpty() ?: false && box.isFocused) {
          box.show()
        }
      } else {
        if (items.size == 1) {
          // We need to get the String differently depending on the
          // nature
          // of the object.
          val item = items[0]

          // To get the value we want to compare with the written
          // value, we need to crop the value according to the current
          // selectionCrop.
          val comparableItem = item

          if (value.get() == comparableItem) {
            Platform.runLater { box.hide() }
          }
        }
      }

      box.editor.text = value.get()
    }
  }


  class InputFilter<T> @JvmOverloads constructor(
    val box: ComboBox<T>, val items: FilteredList<T>, val upperCase: Boolean = false, val maxLength: Int = -1,
    val restriction: String? = null
  ) : ChangeListener<String> {
    private var count = 0

    /**
     * @param box   The combo box to whose textProperty this listener is
     * added.
     * @param items The [FilteredList] containing the items in the list.
     */
    init {
      this.box.setItems(items)
      this.box.showingProperty().addListener { observable, oldValue, newValue ->
        if (newValue == false) {
          items.setPredicate(null)
          box.parent.requestFocus()
        }
      }
    }

    override fun changed(observable: ObservableValue<out String>, oldValue: String, newValue: String) {
      val value: StringProperty = SimpleStringProperty(newValue)
      count++
      println(this.count)
      println(oldValue)
      println(newValue)
      // If any item is selected we save that reference.
      val selected = if (box.selectionModel.selectedItem != null
      ) box.selectionModel.selectedItem else null

      var selectedString: String? = null
      // We save the String of the selected item.
      if (selected != null) {
        selectedString = box.converter.toString(selected)
      }

      if (upperCase) {
        value.set(value.get().uppercase(Locale.getDefault()))
      }

      if (maxLength >= 0 && value.get().length > maxLength) {
        value.set(oldValue)
      }

      if (restriction != null) {
        if (!value.get().matches("$restriction*".toRegex())) {
          value.set(oldValue)
        }
      }

      // If an item is selected and the value in the editor is the same
      // as the selected item we don't filter the list.
      if (selected != null && value.get() == selectedString) {
        // This will place the caret at the end of the string when
        // something is selected.
        println(value.get())
        println(selectedString)
        Platform.runLater { box.editor.end() }
      } else {
        items.setPredicate { item: T ->
          println("setPredicate")
          println(value.get())
          val itemString = item
          if (box.converter.toString(itemString).uppercase(Locale.getDefault())
              .contains(value.get().uppercase(Locale.getDefault()))
          ) {
            return@setPredicate true
          } else {
            return@setPredicate false
          }
        }
      }

      // If the popup isn't already showing we show it.
      if (!box.isShowing) {
        // If the new value is empty we don't want to show the popup,
        // since
        // this will happen when the combo box gets manually reset.
        if (!newValue.isEmpty() && box.isFocused) {
          box.show()
        }
      } else {
        if (items.size == 1) {
          // We need to get the String differently depending on the
          // nature
          // of the object.
          val item = items[0]

          // To get the value we want to compare with the written
          // value, we need to crop the value according to the current
          // selectionCrop.
          val comparableItem = item

          if (value.get() == comparableItem) {
            Platform.runLater { box.hide() }
          }
        }
      }

      box.editor.text = value.get()
    }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }



}