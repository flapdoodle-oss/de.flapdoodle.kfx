/*
 * Copyright (C) 2022
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.kfx.controls.grapheditor

import de.flapdoodle.kfx.controls.grapheditor.types.EdgeId
import de.flapdoodle.kfx.controls.grapheditor.types.IsSelectable
import de.flapdoodle.kfx.controls.grapheditor.types.VertexSlotId
import de.flapdoodle.kfx.extensions.PseudoClassWrapper
import de.flapdoodle.kfx.shapes.Arrow
import de.flapdoodle.kfx.shapes.Curves
import de.flapdoodle.kfx.strokes.LinearGradients
import de.flapdoodle.kfx.types.ColoredAngleAtPoint2D
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.css.PseudoClass
import javafx.geometry.Bounds
import javafx.scene.Parent
import javafx.scene.layout.Region
import javafx.scene.paint.Color

class Edge(
  val start: VertexSlotId,
  val end: VertexSlotId
): Region(), IsSelectable {
  val edgeId = EdgeId(start, end)

  companion object {
    fun onlyConnections(node: javafx.scene.Node): List<Edge> {
      return if (node is Parent) {
        node.childrenUnmodifiable.filterIsInstance<Edge>()
      } else {
        emptyList()
      }
    }
  }

  private object Style {
    val Focused = PseudoClassWrapper<Edge>(PseudoClass.getPseudoClass("focused"))
    val Selected = PseudoClassWrapper<Edge>(PseudoClass.getPseudoClass("selected"))
  }


  val registry = SimpleObjectProperty<Registry>()
  
  private val startVertex = SimpleObjectProperty<Vertex?>()
  private val endVertex = SimpleObjectProperty<Vertex?>()
//  private val line = Line(0.0, 0.0, 100.0, 50.0)

//  private val startConnector = ValueOfValueBinding.of(startNode, Node::someFakeConnector)
//    .defaultIfNull(Values.constantObject(AngleAtPoint2D(Point2D(0.0, 0.0), 0.0)))
//  private val endConnector = ValueOfValueBinding.of(endNode, Node::someFakeConnector)
//    .defaultIfNull(Values.constantObject(AngleAtPoint2D(Point2D(0.0, 0.0), 0.0)))
  private val startConnector = SimpleObjectProperty(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK))
  private val endConnector = SimpleObjectProperty(ColoredAngleAtPoint2D(0.0, 0.0, 0.0, Color.BLACK))
  private val selected = SimpleBooleanProperty(false)

  private val curve = Curves.cubicCurve(startConnector, endConnector)
  private val arrow = Arrow(endConnector)

  init {
    styleClass.addAll("edge")
    stylesheets += javaClass.getResource("Edge.css").toExternalForm()
    //isFocusTraversable = false

//    val linearGrad = LinearGradient(
//      0.0,  // start X
//      0.0,  // start Y
//      1.0,  // end X
//      1.0,  // end Y
//      true,  // proportional
//      CycleMethod.NO_CYCLE,  // cycle colors
//      // stops
//      Stop(0.1, Color.rgb(255, 0, 0, .991)),
//      Stop(1.0, Color.rgb(0, 255, 0, .991))
//    )

    val colorGradient = LinearGradients.exact(
      startConnector.map { it.point2D },
      endConnector.map { it.point2D },
      startConnector.map { it.color },
      endConnector.map { it.color }
    )

    children.add(curve.apply {
      styleClass.addAll("path")
      
//      stroke = linearGrad
      strokeProperty().bind(colorGradient)
      fill = Color.TRANSPARENT
      isPickOnBounds = false
    })
    children.add(arrow.apply {
      styleClass.addAll("path")
      strokeProperty().bind(colorGradient)
      fillProperty().bind(colorGradient)
      isPickOnBounds = false
    })
    isPickOnBounds = false

    selected.subscribe { it ->
      if (it) Style.Selected.enable(this) else Style.Selected.disable(this)
    }
  }

  fun init(resolver: (VertexSlotId) -> ObjectBinding<ColoredAngleAtPoint2D>) {
    startConnector.bind(resolver(start).map { ColoredAngleAtPoint2D(sceneToLocal(it.point2D), it.angle, it.color) })
    endConnector.bind(resolver(end).map { ColoredAngleAtPoint2D(sceneToLocal(it.point2D), it.angle, it.color) })
  }

  fun dispose() {
    startVertex.unbind()
    endVertex.unbind()
  }

  fun boundsInParent(): Bounds {
    return curve.boundsInParent
  }

  fun selectedProperty(): ReadOnlyProperty<Boolean> = selected

  override fun isSelected(): Boolean {
    return selected.get()
  }

  override fun select(value: Boolean) {
    selected.value = value
  }

  fun focus() {
    Style.Focused.enable(this)
  }

  fun blur() {
    Style.Focused.disable(this)
  }

}