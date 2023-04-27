package de.flapdoodle.kfx.controls.table

import de.flapdoodle.kfx.extensions.cssClassName
import javafx.collections.ObservableList
import javafx.scene.control.Control
import javafx.scene.control.SkinBase
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class SlimFooter<T : Any>(
  private val columns: ObservableList<out Column<T, out Any>>
) : Control() {

  private val skin = SlimFooterSkin(this)

  init {
    cssClassName("slim-footer")
  }

  internal fun columnsChanged() {
    skin.columnsChanged()
  }

  override fun createDefaultSkin() = skin

  class SlimFooterSkin<T : Any>(
    private val src: SlimFooter<T>
  ) : SkinBase<SlimFooter<T>>(src) {
    private val footer = HBox().apply {
    }

    internal fun columnsChanged() {
      val columns = src.columns.map { FooterColumn(it).apply {
//        prefWidthProperty().bind(it.widthProperty())
        }
      }

      footer.children.setAll(columns)
    }

    init {
      children.add(footer)
    }
  }

  class FooterColumn<T: Any>(
    internal val column: Column<T, out Any>
  ) : Control() {

    private val skin = Skin(this)
    override fun createDefaultSkin() = skin

    class Skin<T: Any>(control: FooterColumn<T>) : SkinBase<FooterColumn<T>>(control) {
      val stackPane = StackPane()

      init {
        if (control.column.footer!=null) {
          stackPane.children.add(control.column.footer.invoke())
        }
        children.add(stackPane)
      }

    }
  }
}