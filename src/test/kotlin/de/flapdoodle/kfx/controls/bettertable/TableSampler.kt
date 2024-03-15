package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.ReadOnlyState
import de.flapdoodle.kfx.converters.Converters
import de.flapdoodle.kfx.extensions.withAnchors
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage

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
            header = { it -> LabelHeaderColumn(it,"A") },
            cell = {
              Cell(
                row = it,
                value = it.age,
                converter = Converters.converterFor(Int::class),
                editable = true
              )
            },
            setter = { row, v -> row.copy(age = v ?: 0) },
            footer = { LabelFooterColumn(it, "a") },
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

      val splitPane = SplitPane(
        Table(rows, columns, changeListener),
        Table(rows, columns, changeListener) { ReadOnlyState() },
      )

      val content = AnchorPane(
        splitPane.withAnchors(all = 10.0, bottom = 50.0),
        HBox(Button("B").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              header = { LabelHeaderColumn(it,"B") },
              cell = { Cell(it, it.name, Converters.converterFor(String::class), true) },
              setter = { row, v -> row.copy(name = v ?: "") }
            )
          }
        }, Button("B(r)").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              header = { LabelHeaderColumn(it,"B*", false) },
              cell = { Cell(it, it.name, Converters.converterFor(String::class), false) },
              setter = { row, v -> row.copy(name = v ?: "") }
            )
          }
        }, Button("C").apply {
          onAction = EventHandler {
            columns.value += CustomColumn(
              header = { LabelHeaderColumn(it,"C").apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              cell = { Cell(it, it.size, Converters.converterFor(Double::class), true).apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              setter = { row, v -> row.copy(size = v ?: 1.5) },
              footer = { LabelFooterColumn(it,"c").apply {
                backgroundProperty().bind(backGroundToggled)
              } }
            )
          }
        }, Button("C(r)").apply {
          onAction = EventHandler {
            columns.value += Column(
              header = { LabelHeaderColumn(it,"C*", false).apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              cell = { Cell(it, it.size, Converters.converterFor(Double::class), false).apply {
                backgroundProperty().bind(backGroundToggled)
              } },
              footer = { LabelFooterColumn(it,"c*", false).apply {
                backgroundProperty().bind(backGroundToggled)
              } }
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

  class LabelHeaderColumn(
    override val column: Column<Row, out Any>,
    label: String,
    editable: Boolean = true
  ) : HeaderColumn<Row>(column, editable) {
    init {
      setContent(Label(label))
    }
  }

  class LabelFooterColumn(
    override val column: Column<Row, out Any>,
    label: String,
    editable: Boolean = true
  ) : FooterColumn<Row>(column, editable) {
    init {
      setContent(Label(label))
    }
  }

  class CustomColumn<C : Any>(
    header: (Column<Row, out Any>) -> HeaderColumn<Row>,
    cell: (Row) -> Cell<Row, C>,
    val setter: (Row, C?) -> Row,
    footer: ((Column<Row, out Any>) -> FooterColumn<Row>)? = null
  ) : Column<Row, C>(header, cell, footer) {
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