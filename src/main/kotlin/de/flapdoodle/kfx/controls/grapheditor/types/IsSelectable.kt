package de.flapdoodle.kfx.controls.grapheditor.types

interface IsSelectable {
  fun isSelected(): Boolean
  fun select(value: Boolean)

  companion object {
    fun <T: IsSelectable> select(multi: Boolean, item: T, all: List<T>) {
      if (!multi) {
        var selected = 0
        all.forEach {
          if (it.isSelected()) selected += 1
        }
        if (selected>1) {
          item.select(true)
        } else
          item.select(!item.isSelected())

        all.forEach {
          if (item != it) it.select(false)
        }
      } else {
        item.select(!item.isSelected())
      }
    }
  }
}