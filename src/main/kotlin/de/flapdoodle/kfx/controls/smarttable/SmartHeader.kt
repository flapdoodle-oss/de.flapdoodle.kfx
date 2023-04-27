package de.flapdoodle.kfx.controls.smarttable

import de.flapdoodle.kfx.extensions.cssClassName
import de.flapdoodle.kfx.layout.splitpane.BetterSplitPane
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase

class SmartHeader<T : Any>(
    private val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartHeaderSkin(this)

  init {
    isFocusTraversable = false
    cssClassName("smart-header")
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  class SmartHeaderSkin<T : Any>(
      private val src: SmartHeader<T>
  ) : SkinBase<SmartHeader<T>>(src) {
    private val header = BetterSplitPane().apply {
    }

    internal fun columnsChanged() {
      header.nodes().setAll(src.columns/* + Label("   ").apply { maxWidth = Double.MAX_VALUE }*/)
    }

    init {
      children.add(header)
    }
  }

}