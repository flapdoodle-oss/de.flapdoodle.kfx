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

import de.flapdoodle.kfx.clone.AutoScrollingWindow
import de.flapdoodle.kfx.clone.BoxFactory
import de.flapdoodle.kfx.clone.GraphEditorView
import de.flapdoodle.tab.controls.tables.SmartCell
import de.flapdoodle.tab.controls.tables.SmartColumn
import de.flapdoodle.tab.controls.tables.SmartTable
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter

class SmartTableSampler : Application() {

    override fun start(stage: Stage) {
        val rows = FXCollections.observableArrayList(Data("Klaus", 21))

        val nameColumn = object : SmartColumn<Data, String>(Label("name")) {
            override fun cell(row: Data): SmartCell<Data, String> {
                return object : SmartCell<Data, String>(
                    row.name,
                    true,
                    DefaultStringConverter()
                ) {}
            }
        }
        val ageColumn = object : SmartColumn<Data, Int>(Label("age")) {
            override fun cell(row: Data): SmartCell<Data, Int> {
                return object : SmartCell<Data, Int>(
                    row.age,
                    true,
                    IntegerStringConverter()
                ) {}
            }
        }
        val columns = FXCollections.observableArrayList(nameColumn, ageColumn)

        val table = SmartTable<Data>(
            rows,
            columns
        )
        stage.scene = Scene(table, 600.0, 400.0)
        stage.show()
    }

    class Data(var name: String, var age: Int) {

    }
}