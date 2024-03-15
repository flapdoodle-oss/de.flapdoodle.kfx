package de.flapdoodle.kfx.controls.bettertable

open class FooterColumn<T: Any>(
  column: Column<T, out Any>,
  editable: Boolean = true,
) : AbstractHeaderColumn<T>(column, "footer-column", editable) {
  init {
    isFocusTraversable = false
  }
}
