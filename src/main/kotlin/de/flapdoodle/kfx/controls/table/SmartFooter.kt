package de.flapdoodle.tab.controls.tables

import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class SmartFooter<T : Any>(
    private val columns: ObservableList<out SmartColumn<T, out Any>>
) : Control() {

  private val skin = SmartFooterSkin(this)

  init {
//    addClass(SmartTableStyles.smartFooter)
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  class SmartFooterSkin<T : Any>(
      private val src: SmartFooter<T>
  ) : SkinBase<SmartFooter<T>>(src) {
    private val footer = HBox().apply {
    }


    internal fun columnsChanged() {
      footer.children.setAll(src.columns.map { FooterColumn(it).apply {
        prefWidthProperty().bind(it.widthProperty())
      } })
    }

    init {
      children.add(footer)
    }
  }

  class FooterColumn<T: Any>(
      private val column: SmartColumn<T, out Any>
  ) : Control() {

    private val skin = Skin(this)
    override fun createDefaultSkin() = skin

    class Skin<T: Any>(control: FooterColumn<T>) : SkinBase<FooterColumn<T>>(control) {
      val stackPane = StackPane()

      init {
        if (control.column.footer!=null) {
          stackPane.children.add(control.column.footer)
        }
        children.add(stackPane)
      }

    }
  }
}