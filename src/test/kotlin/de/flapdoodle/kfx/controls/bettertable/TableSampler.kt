package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import javafx.util.StringConverter

class TableSampler {
  class Sample : Application() {
    override fun start(stage: Stage) {

      val toggle = SimpleObjectProperty(false)
      val backGroundToggled = toggle.map {
        if (it)
          Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
        else null
      }

      val columns = SimpleObjectProperty<List<Column<Row, out Any>>>(
        listOf(
          CustomColumn(
            label = "A",
            property = { it.age },
            converter = Converters.converterFor(Int::class),
            editable = true,
            setter = { row, v -> row.copy(age = v ?: 0) },
          )
        )
      )

      val rows = SimpleObjectProperty(
        listOf(
          Row(20, "Klaus", 1.89),
          Row(30, "Susi", 1.79),
          Row(28, "Peter", 1.67),
          Row(17, "Claudia", 1.93),
          Row(19, "Joane", 1.88),
          Row(44, "Achim", 1.82),
          Row(67, "Thorsten", 1.78),
        )
      )

      val changeListener = CellChangeListener<Row> { row, change ->
        println("change: $row -> $change")
        val changed = when (val column = change.column) {
          is CustomColumn<out Any> -> {
            column.change(row, change)
          }
          else -> {
            row
          }
        }

        val list = rows.value
        val index = list.indexOf(row)

        rows.value = list.subList(0, index) + changed + list.subList(index+1, list.size)
        
        changed
      }

      val headerColumnFactory = HeaderColumnFactory.Default<Row>().andThen { column, headerColumn ->
        if (column.label.contains("*")) {
          headerColumn.backgroundProperty().bind(backGroundToggled)
        }
      }
      val cellFactory = CellFactory.Default<Row>().andThen { column, cell ->
        if (column.label.contains("*")) {
          cell.backgroundProperty().bind(backGroundToggled)
        }
      }
      val footerColumnFactory = FooterColumnFactory.Default<Row>().andThen { column, footerColumn ->
        if (column.label.contains("*")) {
          footerColumn.backgroundProperty().bind(backGroundToggled)
        }
      }

      val splitPane = SplitPane(
        Table(rows, columns, changeListener, headerColumnFactory, cellFactory, footerColumnFactory),
        Table(rows, columns, changeListener, headerColumnFactory, cellFactory, footerColumnFactory) { ReadOnlyState(it) },
      )

      val content = AnchorPane(
        splitPane.withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              label = "B",
              property = { it.name },
              converter = Converters.converterFor(String::class),
              editable = true,
              setter = { row, v -> row.copy(name = v ?: "") }
            )
          }
        }, Button("B(r)").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              label = "B*",
              property = { it.name },
              converter = Converters.converterFor(String::class),
              editable = false,
              setter = { row, v -> row.copy(name = v ?: "") }
            )
          }
        }, Button("C").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              label = "C",
              property = { it.size },
              converter = Converters.converterFor(Double::class),
              editable = true,
              setter = { row, v -> row.copy(size = v ?: 1.5) }
            )
          }
        }, Button("C(r)").apply {
          onAction = EventHandler {
            columns.value += Column(
              label = "C*",
              property = { it.size },
              converter = Converters.converterFor(Double::class),
              editable = false
            )
          }
        }, Button("toggle").apply {
          onAction = EventHandler {
            toggle.value = !toggle.value
          }
        }).withAnchors(bottom = 0.0)
      )
      stage.scene = Scene(content, 800.0, 600.0)
      stage.show()
    }

  }

  data class Row(
    val age: Int,
    val name: String,
    val size: Double,
  )

  class CustomColumn<C : Any>(
    override val label: String,
    override val property: (Row) -> C?,
    override val converter: StringConverter<C>,
    override val editable: Boolean,
    override val textAlignment: TextAlignment = TextAlignment.LEFT,
    val setter: (Row, C?) -> Row
  ) : Column<Row, C>(label, property, converter, editable, textAlignment) {
    fun change(row: Row, change: CellChangeListener.Change<Row, out Any>): Row {
      return if (change.column == this) {
        setter(row, change.value as C?)
      } else row
    }

  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application.launch(Sample::class.java, *args)
    }
  }
}