package de.flapdoodle.kfx.layout.grid

import de.flapdoodle.kfx.controls.textfields.TypedTextField
import de.flapdoodle.kfx.types.Id
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage

class WeightGridTableSampler {
  class Sample : Application() {

    override fun start(stage: Stage) {

      val model = SimpleObjectProperty(
        listOf<Person>(
          Person("Anna", 24),
          Person("Peter", 19),
          Person("Susi", 21)
        )                             
      )

      val nameColumn = WeightGridTable.Column<Person>(nodeFactory = { Label(it.name) to WeightGridTable.ChangeListener { } })
      val ageColumn = WeightGridTable.Column<Person>(weight = 2.0, nodeFactory = {
        val textField = TypedTextField(Int::class).apply {
          set(it.age)
          valueProperty().addListener { observable, oldValue, newValue ->
            model.value = model.value.map { p -> if (p.id == it.id) it.copy(age = get()) else p }
          }
        }

        textField to WeightGridTable.ChangeListener { textField.set(it.age) }
      })
      val actionColumn = WeightGridTable.Column<Person>(weight = 1.0, nodeFactory = { t ->
        val button = Button("-").apply {
          onAction = EventHandler {
            model.value = model.value.filter { p -> p.id != t.id }
          }
        }
        button to WeightGridTable.ChangeListener {

        }
      })

      val columns = listOf(
        nameColumn,
        ageColumn,
        actionColumn
      )
      stage.scene = Scene(WeightGridTable(
        model = model,
        indexOf = Person::id,
        columns = columns,
        headerFactory = { values, columns ->
          mapOf(nameColumn to Label("Name"), ageColumn to Label("Age"))
        },
        footerFactory = { values, columns ->
          val name = TextField("")
          val age = TypedTextField(Int::class)
          val add = Button("+").apply {
            onAction = EventHandler {
              if (name.text != null && name.text.isNotBlank()) {
                model.value = model.value + Person(name.text, age.get())
              }
            }
          }
          mapOf(nameColumn to name, ageColumn to age, actionColumn to add)
        }
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