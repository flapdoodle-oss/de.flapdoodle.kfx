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
package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.controls.fields.TypedTextField
import de.flapdoodle.kfx.layout.grid.TableCell.Companion.with
import de.flapdoodle.kfx.types.Id
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.controlsfx.control.spreadsheet.Grid

class GridTableSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val model = SimpleObjectProperty(
        listOf<Person>(
          Person("Anna", 24),
          Person("Peter", 19),
          Person("Susi", 21)
        )                             
      )

      model.addListener { observable, oldValue, newValue ->
        println("changed: $newValue")
      }

      val nameColumn = GridTable.Column<Person>(cellFactory = { TableCell(Label(it.name)) }, horizontalPosition = HPos.LEFT)
      val ageColumn = GridTable.Column<Person>(weight = 2.0, cellFactory = {
        val textField = TypedTextField(Int::class).apply {
          set(it.age)
          valueProperty().addListener { observable, oldValue, newValue ->
            println("value changed: ${it.id} -> ${get()}")
            model.value = model.value.map { p -> if (p.id == it.id) it.copy(age = get()) else p }
          }
        }
        TableCell.initializedWith(it)
          .node(textField)
          .map(Person::age)
          .updateWith(TypedTextField<Int>::set)
        with(textField).map(Person::age).updateWith(TypedTextField<Int>::set)
      })
      val actionColumn = GridTable.Column<Person>(weight = 1.0, cellFactory = { t ->
        val button = Button("-").apply {
          onAction = EventHandler {
            model.value = model.value.filter { p -> p.id != t.id }
          }
        }
        TableCell(button)
      })

      val columns = listOf(
        nameColumn,
        ageColumn,
        actionColumn
      )
      stage.scene = Scene(GridTable(
        model = model,
        indexOf = Person::id,
        columns = columns,
        headerFactories = listOf(
          GridTable.HeaderFooterFactory { values, _ ->
            mapOf(Label("--- all columns ---") to GridTable.Span(start = nameColumn, end = actionColumn))
          },
          GridTable.HeaderFooterFactory { values, _ -> mapOf(
            Label("Name") to GridTable.Span(nameColumn),
            Label("Age") to GridTable.Span(ageColumn)
          )
        }),
        footerFactories = listOf(
          GridTable.HeaderFooterFactory { values, _ ->
            val name = TextField("")
            val age = TypedTextField(Int::class)
            val add = Button("+").apply {
              onAction = EventHandler {
                if (name.text != null && name.text.isNotBlank()) {
                  model.value = model.value + Person(name.text, age.get())
                }
              }
            }
            mapOf(name to GridTable.Span(nameColumn), age to GridTable.Span(ageColumn), add to GridTable.Span(actionColumn))
          },
          GridTable.HeaderFooterFactory { values, _ ->
            mapOf(Label("--- all columns ---") to GridTable.Span(start = nameColumn, end = actionColumn))
          })
      ).apply {
        verticalSpace().set(5.0)
        horizontalSpace().set(10.0)
      })
      stage.show()
    }
  }

  data class Person(val name: String, val age: Int?, val id: Id<Person> = Id.nextId(Person::class))

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}