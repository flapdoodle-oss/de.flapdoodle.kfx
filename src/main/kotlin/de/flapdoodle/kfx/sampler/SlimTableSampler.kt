/**
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
package de.flapdoodle.kfx.sampler

import de.flapdoodle.kfx.bindings.map
import de.flapdoodle.kfx.controls.table.Column
import de.flapdoodle.kfx.controls.table.SlimCell
import de.flapdoodle.kfx.controls.table.SlimTable
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.control.skin.TableColumnHeader
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Callback
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter
import org.controlsfx.control.spreadsheet.SpreadsheetView
import org.controlsfx.control.tableview2.TableColumn2
import org.controlsfx.control.tableview2.TableView2
import org.controlsfx.control.tableview2.cell.TextField2TableCell


class SlimTableSampler : Application() {

  override fun start(stage: Stage) {
    val rows = FXCollections.observableArrayList(
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
      Data("Klaus", 21),
      Data("Ich", 1),
      Data("Du", 100),
    )

    val toogle = SimpleObjectProperty(false)

    val splitPane = SplitPane()
    splitPane.items.add(StackPane().apply {
      children.add(slimTableSample(rows, toogle))
    })
    splitPane.items.add(StackPane().apply {
//      children.add(tableSample(rows, toogle))
//      children.add(spreadsheet(rows, toogle))
      children.add(tableView2(rows, toogle))
    })
    splitPane.setDividerPositions(0.5)


    val wrapper = AnchorPane()
    wrapper.children.add(splitPane.withAnchors(all = 10.0))
    wrapper.children.add(Button("toggle").apply {
      withAnchors(right = 0.0, top = 0.0)
      onAction = EventHandler {
        toogle.value = !toogle.value
//                if (columns.contains(ageColumn)) {
//                    columns.remove(ageColumn)
//                } else {
//                    columns.add(ageColumn)
//                }
      }
    })

    stage.scene = Scene(wrapper, 800.0, 600.0)
    stage.show()
  }

  private fun spreadsheet(data: ObservableList<Data>, toggle: ObservableValue<Boolean>): Node {
    val backGroundToogled = toggle.map {
      if (it)
        Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
      else null
    }

    val nameColumn = Column<Data, String>(
      header = { Label("name") },
      cell = { it ->
        SlimCell<Data, String>(
          it.name,
          DefaultStringConverter(),
          true
        )
      },
      footer = { Label("N") }
    )

    val ageColumn = Column<Data, Int>(
      header = { Label("age").apply {
        this.backgroundProperty().bind(backGroundToogled)
      } },
      cell = { it ->
        SlimCell<Data, Int>(
          it.age,
          IntegerStringConverter(),
          true
        ).apply {
          this.backgroundProperty().bind(backGroundToogled)
        }
      },
      footer = { Label("A") }
    )
    val columns = FXCollections.observableArrayList(nameColumn, ageColumn)

    val table = SpreadsheetView()
//    table.columns = columns
    return table
  }

  private fun slimTableSample(data: ObservableList<Data>, toggle: ObservableValue<Boolean>): Node {
    val backGroundToogled = toggle.map {
      if (it)
        Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
      else null
    }

    val nameColumn = Column<Data, String>(
      header = { Label("name") },
      cell = { it ->
        SlimCell<Data, String>(
          it.name,
          DefaultStringConverter(),
          true
        )
      },
      footer = { Label("N") }
    )

    val ageColumn = Column<Data, Int>(
      header = { Label("age").apply {
        this.backgroundProperty().bind(backGroundToogled)
      } },
      cell = { it ->
        SlimCell<Data, Int>(
          it.age,
          IntegerStringConverter(),
          true
        ).apply {
          this.backgroundProperty().bind(backGroundToogled)
        }
      },
      footer = { Label("A") }
    )
    val columns = FXCollections.observableArrayList(nameColumn, ageColumn)

    val table = SlimTable<Data>(
      data,
      columns
    ) { row, change ->
      println("change ($row, ${change.column}) to ${change.value}")
      val source: Data = data[row]
      when (change.column) {
        nameColumn -> source.name = change.value as String?
        ageColumn -> source.age = change.value as Int?
      }
      data[row] = source
    }
    return table
  }

  private fun tableSample(data: ObservableList<Data>, toggle: ObservableValue<Boolean>): Node {
    // https://jenkov.com/tutorials/javafx/tableview.html
    val table = TableView<Data>()

    val backGroundToogled = toggle.map {
      if (it)
        Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
      else null
    }

    val nameColumn: TableColumn<Data, String> = TableColumn("Name")
    nameColumn.isSortable = false
    nameColumn.cellValueFactory = PropertyValueFactory("name")
    nameColumn.cellFactory = TextFieldTableCell.forTableColumn()
    nameColumn.setOnEditCommit { event ->
      event.tableView.items[event.tablePosition.row].name = event.newValue
    }

    val ageColumn: TableColumn<Data, Int> = TableColumn("Age")
    ageColumn.isSortable = false
    ageColumn.cellValueFactory = PropertyValueFactory("age")
    ageColumn.cellFactory = TextFieldTableCell.forTableColumn<Data?, Int?>(IntegerStringConverter()).andThen {
      it.backgroundProperty().bind(backGroundToogled)
    }
    ageColumn.setOnEditCommit { event ->
      event.tableView.items[event.tablePosition.row].age = event.newValue
    }

    ageColumn.graphic = Pane().apply {
      children.add(Label("!"))
      backgroundProperty().bind(backGroundToogled)
    }
//    ageColumn.style = "-fx-background-color: red"

    table.columns.add(nameColumn)
    table.columns.add(ageColumn)

    Bindings.bindContent(table.items, data)

//        table.getItems().add(
//            Person("John", "Doe")
//        )
//        tableView.getItems().add(
//            Person("Jane", "Deer")
//        )
    table.isEditable = true
    table.isTableMenuButtonVisible = true


    return table
  }

  private fun tableView2(data: ObservableList<Data>, toggle: ObservableValue<Boolean>): Node {
    val table = TableView2<Data>()

    val backGroundToogled = toggle.map {
      if (it)
        Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
      else null
    }

    val nameColumn: TableColumn2<Data, String> = TableColumn2("Name")
    nameColumn.isSortable = false
    nameColumn.cellValueFactory = PropertyValueFactory("name")
    nameColumn.cellFactory = TextField2TableCell.forTableColumn()
    nameColumn.setOnEditCommit { event ->
      event.tableView.items[event.tablePosition.row].name = event.newValue
    }
    val labelFirstName=Label()
    labelFirstName.textProperty()
      .bind(Bindings.createStringBinding({ "#1: " + table.items.filtered { n -> n.age == 21 }.count()}, table.items))
    nameColumn.southNode = labelFirstName

    val ageColumn: TableColumn2<Data, Int> = TableColumn2("Age")
    ageColumn.isSortable = false
    ageColumn.cellValueFactory = PropertyValueFactory("age")
    ageColumn.cellFactory = TextField2TableCell.forTableColumn<Data?, Int?>(IntegerStringConverter()).andThen {
      it.backgroundProperty().bind(backGroundToogled)
    }
    ageColumn.setOnEditCommit { event ->
      event.tableView.items[event.tablePosition.row].age = event.newValue
    }

    ageColumn.graphic = Pane().apply {
      children.add(Label("!!"))
      backgroundProperty().bind(backGroundToogled)
    }

//    ageColumn.style = "-fx-background-color: red"

    table.columns.add(nameColumn)
    table.columns.add(ageColumn)

    Bindings.bindContent(table.items, data)
//    table.fixedRows.setAll(2,4)

//        table.getItems().add(
//            Person("John", "Doe")
//        )
//        tableView.getItems().add(
//            Person("Jane", "Deer")
//        )
    table.isEditable = true
    table.isRowHeaderVisible = true
//    table.rowHeader = ageColumn


    return table
  }

  fun <S, D> Callback<S, D>.andThen(onCreated: (D) -> Unit): Callback<S, D> {
    val that = this
    return Callback<S, D> { param ->
      val ret = that.call(param)
      onCreated(ret)
      ret
    }
  }

  class Data(var name: String?, var age: Int?) {

  }
}