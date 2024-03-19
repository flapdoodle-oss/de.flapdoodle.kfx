package de.flapdoodle.kfx.controls.bettertable

class HeaderColumn<T: Any>(
  column: Column<T, out Any>,
) : AbstractHeaderColumn<T>(column, "header-column") {
}
