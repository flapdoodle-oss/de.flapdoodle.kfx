package de.flapdoodle.kfx.nodeeditor

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.BoundingBox
import javafx.geometry.Bounds
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import kotlin.math.floor

class Background : Region() {
  private val HALF_PIXEL_OFFSET = -0.5;
  private val color: Color = Color.rgb(220, 220, 220)
  private val grid = Path()
  private val border = Rectangle(10.0, 10.0)

  //  private val scrollXBounds = SimpleObjectProperty<ScrollBounds>(ScrollBounds(0.0,10.0,2.0))
//  private val scrollYBounds = SimpleObjectProperty<ScrollBounds>(ScrollBounds(0.0,10.0,2.0))
  private val bounds = SimpleObjectProperty<Bounds>(BoundingBox(-50.0, -50.0, 100.0, 100.0))

  init {
    isManaged = false
    isMouseTransparent = true

    border.fill = Color.DARKGRAY
//    children.add(Rectangle(200.0, 200.0).apply {
//      fill = Color.RED
//    })
    grid.stroke=color
    children.add(grid)
    children.add(border)

//    bounds.subscribe { it -> redraw(it) }
  }

  override fun layoutChildren() {
    super.layoutChildren()

//    draw(300.0, 300.0)
//    redraw(bounds.value)
//    redraw(boundsInLocal)
//    val bounds = boundsInLocal
//    border.resizeRelocate(0.0, 0.0, width, height)
//    border.width = width
//    border.height = height
  }

  fun redraw(bounds: Bounds) {
    grid.elements.clear()

    val pWidth = bounds.width
    val pHeight = bounds.height
    val spacing: Double = 10.0

    val hLineCount = floor((pHeight + 1) / spacing).toInt()
    val vLineCount = floor((pWidth + 1) / spacing).toInt()
    
    val xOffset = ((bounds.minX + 1) / spacing).toInt() * spacing
    val yOffset = ((bounds.minY + 1) / spacing).toInt() * spacing

    for (i in 0 until hLineCount) {
      val y: Double = yOffset + (i + 1) * spacing + HALF_PIXEL_OFFSET
      grid.getElements().add(MoveTo(bounds.minX, y))
      grid.getElements().add(LineTo(pWidth, y))
    }

    for (i in 0 until vLineCount) {
      val x: Double = xOffset + (i + 1) * spacing + HALF_PIXEL_OFFSET
      grid.getElements().add(MoveTo(x, bounds.minY))
      grid.getElements().add(LineTo(x, pHeight))
    }
  }

  fun draw(pWidth: Double, pHeight: Double) {
    val spacing: Double = 10.0
    val hLineCount = floor((pHeight + 1) / spacing).toInt()
    val vLineCount = floor((pWidth + 1) / spacing).toInt()

    for (i in 0 until hLineCount) {
      val y: Double = (i + 1) * spacing + HALF_PIXEL_OFFSET
      grid.getElements().add(MoveTo(0.0, y))
      grid.getElements().add(LineTo(pWidth, y))
    }

    for (i in 0 until vLineCount) {
      val x: Double = (i + 1) * spacing + HALF_PIXEL_OFFSET
      grid.getElements().add(MoveTo(x, 0.0))
      grid.getElements().add(LineTo(x, pHeight))
    }
  }

  fun bind(source: ObservableValue<Bounds>) {
//    this.bounds.bind(source)
    source.subscribe { it ->
//      println("---> $it")
//      border.resizeRelocate(it.minX, it.minY, it.width, it.height)
//      resizeRelocate(it.minX, it.minY, it.width, it.height)
      redraw(it)
//      border.relocate(it.minX, it.minY)
//      border.width = it.width
//      border.height = it.height
    }
  }
}