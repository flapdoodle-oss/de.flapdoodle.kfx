package de.flapdoodle.kfx.controls.bettertable

class FooterColumn<T: Any>(
  column: Column<T, out Any>,
) : AbstractHeaderColumn<T>(column, "footer-column") {
  init {
    isFocusTraversable = false
  }
}
