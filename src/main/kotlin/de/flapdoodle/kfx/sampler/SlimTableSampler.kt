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

import de.flapdoodle.kfx.controls.smarttable.SmartCell
import de.flapdoodle.kfx.controls.smarttable.SmartColumn
import de.flapdoodle.kfx.controls.smarttable.SmartTable
import de.flapdoodle.kfx.controls.table.Column
import de.flapdoodle.kfx.controls.table.SlimCell
import de.flapdoodle.kfx.controls.table.SlimTable
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.TextAlignment
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

        table.withAnchors(all = 10.0)

        val wrapper = AnchorPane()
        wrapper.children.add(table)

        stage.scene = Scene(wrapper, 600.0, 400.0)
        stage.show()
    }

    class Data(var name: String?, var age: Int?) {

    }
}