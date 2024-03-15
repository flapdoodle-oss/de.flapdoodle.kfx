package de.flapdoodle.kfx.controls.bettertable

open class HeaderColumn<T: Any>(
  column: Column<T, out Any>,
  editable: Boolean = true,
) : AbstractHeaderColumn<T>(column, "header-column", editable)
