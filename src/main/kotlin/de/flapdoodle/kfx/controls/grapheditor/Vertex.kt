package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.bindings.NodeContainerProperty
import de.flapdoodle.kfx.bindings.ObjectBindings
import de.flapdoodle.kfx.bindings.and
import de.flapdoodle.kfx.controls.grapheditor.connectors.SlotsPane
import de.flapdoodle.kfx.controls.grapheditor.model.Position
import de.flapdoodle.kfx.controls.grapheditor.model.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.IsSelectable
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.grapheditor.types.VertexId
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.extensions.layoutPosition
import de.flapdoodle.kfx.types.AngleAtPoint2D
import de.flapdoodle.kfx.types.LayoutBounds
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.css.PseudoClass
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.layout.Background
import javafx.scene.paint.Color

class Vertex(
  private val name: String
) : BorderPane(), IsSelectable {
  val vertexId=VertexId()
  val registry = SimpleObjectProperty<Registry>()

  companion object {
    fun onlyNodes(node: javafx.scene.Node): List<Vertex> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<Vertex>()
      } else {
        emptyList<Vertex>()
      }
    }
  }

  private object Style {
    val Focused = PseudoClassWrapper<Vertex>(PseudoClass.getPseudoClass("focused"))
//    val Active = PseudoClassWrapper<Vertex>(PseudoClass.getPseudoClass("active"))
    val Selected = PseudoClassWrapper<Vertex>(PseudoClass.getPseudoClass("selected"))
  }

  private val contentWrapper=StackPane()
  private val _content=NodeContainerProperty.of<javafx.scene.Node>("content", contentWrapper::getChildren)
  private val connectors = FXCollections.observableArrayList<Slot>()
  private val selected = SimpleBooleanProperty(false)
  private val nameProperty = SimpleStringProperty(name)

  var content: javafx.scene.Node
    get() = _content.get()
    set(value) { _content.set(value)}

  fun nameProperty() = nameProperty

  override fun toString(): String {
    return "Vertex(${nameProperty().value})"
  }

  init {
    styleClass.addAll("vertex")
    stylesheets += javaClass.getResource("Vertex.css").toExternalForm()

    setMargin(contentWrapper, Insets(10.0))

    center = contentWrapper
    top = NodeHeader(name).apply {
      Markers.markAsDragBar(this)
    }
    left = SlotsPane(registry, vertexId, connectors, Position.LEFT)
    right = SlotsPane(registry, vertexId, connectors, Position.RIGHT)
    bottom = SlotsPane(registry, vertexId, connectors, Position.BOTTOM)

    selected.subscribe { it ->
      if (it) Style.Selected.enable(this) else Style.Selected.disable(this)
    }
  }

  fun resizeTo(bounds: LayoutBounds) {
    val width = bounds.size.width
    val height = bounds.size.height

    val pW = computePrefWidth(width)
    val pH = computePrefHeight(height)

    prefWidth = width.coerceAtLeast(pW)
    prefHeight = height.coerceAtLeast(pH)
    layoutPosition = bounds.layoutPosition
  }

  fun someFakeHandleCoord(): ObjectBinding<Point2D> {
    return ObjectBindings.merge(layoutXProperty(), layoutYProperty()){ x, y ->
      Point2D(x.toDouble()-5.0,y.toDouble() + 10.0)
    }
  }

  fun someFakeConnector(): ObjectBinding<AngleAtPoint2D> {
    return layoutXProperty().and(layoutYProperty()).map { x, y ->
      AngleAtPoint2D(Point2D(x.toDouble()-5.0, y.toDouble() + 20.0), 0.0)
    }
  }

  fun addConnector(connector: Slot) {
    connectors.add(connector)
  }

  fun removeConnector(slotId: SlotId) {
    connectors.removeIf { it.id==slotId }
  }

  class NodeHeader(label: String) : HBox() {
    init {
      isMouseTransparent = false
      background = Background(BackgroundFill(Color.GREY, CornerRadii(2.0), Insets(1.0)))
      children.add(Label(label).apply {
        setHgrow(this, Priority.ALWAYS)
      })
    }
  }

  fun selectedProperty(): ReadOnlyProperty<Boolean> = selected

  override fun isSelected(): Boolean {
    return selected.get()
  }

  override fun select(value: Boolean) {
    selected.value = value
  }

  @Deprecated("use IsSelectable")
  fun select() {
    selected.value = true
  }

  @Deprecated("use IsSelectable")
  fun deselect() {
    selected.value = false
  }

  fun focused(): Boolean {
    return Style.Focused.enabled(this)
  }

  fun focus() {
    Style.Focused.enable(this)
  }

  fun blur() {
    Style.Focused.disable(this)
  }


}