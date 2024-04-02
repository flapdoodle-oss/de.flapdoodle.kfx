package de.flapdoodle.kfx.controls.bettertable

import de.flapdoodle.kfx.controls.bettertable.events.DefaultState
import de.flapdoodle.kfx.controls.bettertable.events.EventContext
import de.flapdoodle.kfx.controls.bettertable.events.State
import de.flapdoodle.kfx.converters.Converters
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.util.StringConverter
import java.time.LocalDate

object TableFactory {

  fun rows(): SimpleObjectProperty<List<Row>> {
    val rows = SimpleObjectProperty(
      listOf(
        Row(20, "Klaus", 1.89),
        Row(30, "Susi", 1.79, LocalDate.now()),
        Row(28, "Peter", 1.67),
        Row(17, "Claudia", 1.93),
        Row(19, "Joane", 1.88),
        Row(44, "Achim", 1.82),
        Row(67, "Thorsten", 1.78),
      )
    )
    return rows
  }

  fun columns(): SimpleObjectProperty<List<Column<Row, out Any>>> {
    val columns = SimpleObjectProperty<List<Column<Row, out Any>>>(
      listOf(
        CustomColumn(
          label = "Age",
          property = ColumnProperty(Int::class, { it.age }),
          editable = true,
          setter = { row, v -> row.copy(age = v ?: 0) },
        )
      )
    )
    return columns
  }

  fun columnsForEachProperty(): SimpleObjectProperty<List<Column<Row, out Any>>> {
    return SimpleObjectProperty<List<Column<Row, out Any>>>(
      listOf(
        CustomColumn(
          label = "Age",
          property = ColumnProperty(Int::class, { it.age }),
          editable = true,
          setter = { row, v -> row.copy(age = v ?: 0) },
        ),
        CustomColumn(
          label = "Age",
          property = ColumnProperty(String::class, { it.name }),
          editable = true,
          setter = { row, v -> row.copy(name = v) },
        ),
        CustomColumn(
          label = "Size",
          property = ColumnProperty(Double::class, { it.size }),
          editable = true,
          setter = { row, v -> row.copy(size = v) },
        ),
        CustomColumn(
          label = "Birthday",
          property = ColumnProperty(LocalDate::class, { it.birthDay }),
          editable = true,
          setter = { row, v -> row.copy(birthDay = v) },
        )
      )
    )
  }

  fun table(): Table<Row> {
    return table(rows(), columnsForEachProperty(), SimpleObjectProperty(false))
  }

  fun table(
    rows: SimpleObjectProperty<List<Row>>,
    columns: SimpleObjectProperty<List<Column<Row, out Any>>>,
    toggle: ObservableValue<Boolean>,
    stateFactory: (EventContext<Row>) -> State<Row> = { DefaultState(it) }
  ): Table<Row> {

    val backGroundToggled = toggle.map {
      if (it)
        Background(BackgroundFill(Color.rgb(255, 0, 0, 0.2), CornerRadii.EMPTY, Insets.EMPTY))
      else null
    }

//    val columns = SimpleObjectProperty<List<Column<Row, out Any>>>(
//      listOf(
//        CustomColumn(
//          label = "A",
//          property = { it.age },
//          converter = Converters.converterFor(Int::class),
//          editable = true,
//          setter = { row, v -> row.copy(age = v ?: 0) },
//        )
//      )
//    )

//    val rows = SimpleObjectProperty(
//      listOf(
//        Row(20, "Klaus", 1.89),
//        Row(30, "Susi", 1.79),
//        Row(28, "Peter", 1.67),
//        Row(17, "Claudia", 1.93),
//        Row(19, "Joane", 1.88),
//        Row(44, "Achim", 1.82),
//        Row(67, "Thorsten", 1.78),
//      )
//    )

//    if (false) {
//      rows.value = emptyList()
//    }

    val changeListener = object : TableChangeListener<TableFactory.Row> {
      override fun changeCell(row: TableFactory.Row, change: TableChangeListener.CellChange<TableFactory.Row, out Any>): TableFactory.Row {
        println("change: $row -> $change")
        val changed = when (val column = change.column) {
          is TableFactory.CustomColumn<out Any> -> {
            column.change(row, change)
          }
          else -> {
            row
          }
        }
        return changed
      }

      override fun updateRow(row: TableFactory.Row, changed: TableFactory.Row) {
        val list = rows.value
        val index = list.indexOf(row)

        rows.value = list.subList(0, index) + changed + list.subList(index+1, list.size)
      }

      override fun removeRow(row: TableFactory.Row) {
        val list = rows.value
        val index = list.indexOf(row)
        rows.value = list.subList(0, index) + list.subList(index+1, list.size)
      }

      override fun insertRow(index: Int, row: TableFactory.Row): Boolean {
        val list = rows.value
        rows.value = list.subList(0, index) + row + list.subList(index, list.size)
        return true
      }

      override fun emptyRow(index: Int): TableFactory.Row {
        val list = rows.value
        val before = if (index>0 && index<list.size) list[index-1] else null
        val after = if (index<list.size) list[index] else null
        return TableFactory.Row(((before?.age ?: 0) + (after?.age ?: 0)) / 2, null, null)
      }
    }

    val headerColumnFactory = HeaderColumnFactory.Default<TableFactory.Row>().andThen { column, headerColumn ->
      if (column.label.contains("*")) {
        headerColumn.backgroundProperty().bind(backGroundToggled)
      }
    }
    val cellFactory = CellFactory.Default<TableFactory.Row>().andThen { column, cell ->
      if (column.label.contains("*")) {
        cell.backgroundProperty().bind(backGroundToggled)
      }
    }
    val footerColumnFactory = FooterColumnFactory.Default<TableFactory.Row>().andThen { column, footerColumn ->
      if (column.label.contains("*")) {
        footerColumn.backgroundProperty().bind(backGroundToggled)
      }
    }

    return Table(rows, columns, changeListener, headerColumnFactory, cellFactory, footerColumnFactory, stateFactory = stateFactory)
  }

  data class Row(
    val age: Int,
    val name: String?,
    val size: Double?,
    val birthDay: LocalDate? = null
  )

  class CustomColumn<C : Any>(
    override val label: String,
    override val property: ColumnProperty<Row, C>,
//    override val property: (Row) -> C?,
//    override val converter: StringConverter<C>,
    override val editable: Boolean,
    override val textAlignment: TextAlignment = TextAlignment.LEFT,
    val setter: (Row, C?) -> Row
  ) : Column<Row, C>(label, property, editable, textAlignment) {
    fun change(row: Row, change: TableChangeListener.CellChange<Row, out Any>): Row {
      return if (change.column == this) {
        setter(row, change.value as C?)
      } else row
    }

    override fun toString(): String {
      return "CustomColumn($label)"
    }
  }
}