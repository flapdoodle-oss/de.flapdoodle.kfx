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

import de.flapdoodle.kfx.controls.table.Column
import de.flapdoodle.kfx.controls.table.SlimCell
import de.flapdoodle.kfx.controls.table.SlimTable
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter


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

        val nameColumn = Column<Data, String>(
            header = { Label("name") },
            cell = { it -> SlimCell<Data,String>(
                it.name,
                DefaultStringConverter(),
        true
            )},
            footer = { Label("N")}
        )
        
        val ageColumn = Column<Data, Int>(
            header = { Label("age") },
            cell = { it -> SlimCell<Data,Int>(
                it.age,
                IntegerStringConverter(),
                true
            )},
            footer = { Label("A")}
        )
        val columns = FXCollections.observableArrayList(nameColumn, ageColumn)

        val table = SlimTable<Data>(
            rows,
            columns
        ) { row, change ->
            println("change ($row, ${change.column}) to ${change.value}")
            val source: Data = rows[row]
            when (change.column) {
                nameColumn -> source.name = change.value as String?
                ageColumn -> source.age = change.value as Int?
            }
            rows[row] = source
        }

        table//.withAnchors(all = 10.0)

        val splitPane = SplitPane()
        splitPane.items.add(StackPane().apply {
            children.add(table)
        })
        splitPane.items.add(StackPane().apply {
            children.add(tableSample(rows))
        })
        splitPane.setDividerPositions(0.5)


        val wrapper = AnchorPane()
        wrapper.children.add(splitPane.withAnchors(all = 10.0))
        wrapper.children.add(Button("toggle").apply {
            withAnchors(right = 0.0, top = 0.0)
            onAction = EventHandler {
                if (columns.contains(ageColumn)) {
                    columns.remove(ageColumn)
                } else {
                    columns.add(ageColumn)
                }
            }
        })

        stage.scene = Scene(wrapper, 800.0, 600.0)
        stage.show()
    }

    private fun tableSample(data: ObservableList<Data>): Node {
        // https://jenkov.com/tutorials/javafx/tableview.html
        val table = TableView<Data>()

        val column1: TableColumn<Data, String> = TableColumn("Name")
        column1.setCellValueFactory(PropertyValueFactory("name"))
        column1.setCellFactory(TextFieldTableCell.forTableColumn())
        column1.setOnEditCommit { event ->
            event.tableView.items[event.tablePosition.row].name = event.newValue
        }

        val column2: TableColumn<Data, Int> = TableColumn("Age")
        column2.setCellValueFactory(PropertyValueFactory("age"))
        column2.setCellFactory(TextFieldTableCell.forTableColumn(IntegerStringConverter()))
        column2.setOnEditCommit { event ->
            event.tableView.items[event.tablePosition.row].age = event.newValue
        }


        table.getColumns().add(column1)
        table.getColumns().add(column2)

        Bindings.bindContent(table.items, data)

//        table.getItems().add(
//            Person("John", "Doe")
//        )
//        tableView.getItems().add(
//            Person("Jane", "Deer")
//        )
        table.isEditable = true
        

        return table
    }

    class Data(var name: String?, var age: Int?) {

    }
}