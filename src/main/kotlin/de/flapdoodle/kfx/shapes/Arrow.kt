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
package de.flapdoodle.kfx.shapes

import de.flapdoodle.kfx.types.AngleAndPoint2D
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path

class Arrow(
  private val end: ObservableValue<out AngleAndPoint2D>,
  private val size: Double = 10.0,
  private val widthFactor: Double = 1.0
) : Path() {
  init {
    val start = end.map { it.atDistance(size) }
    val left = start.map { it.withDistance(widthFactor * size/2.0, 90.0) }
    val right = start.map { it.withDistance(widthFactor * size/2.0, -90.0) }
    elements.addAll(
      MoveTo().also { moveTo ->
        moveTo.xProperty().bind(left.map { it.x })
        moveTo.yProperty().bind(left.map { it.y })
      },
      LineTo().also { lineTo ->
        lineTo.xProperty().bind(end.map { it.point2D.x })
        lineTo.yProperty().bind(end.map { it.point2D.y })
      },
      LineTo().also { lineTo ->
        lineTo.xProperty().bind(right.map { it.x })
        lineTo.yProperty().bind(right.map { it.y })
      }
    )
  }
}

/*
public class Arrow extends Path{
    private static final double defaultArrowHeadSize = 5.0;

    public Arrow(double startX, double startY, double endX, double endY, double arrowHeadSize){
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);

        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));

        //ArrowHead
        double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //point1
        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
        //point2
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;

        getElements().add(new LineTo(x1, y1));
        getElements().add(new LineTo(x2, y2));
        getElements().add(new LineTo(endX, endY));
    }

    public Arrow(double startX, double startY, double endX, double endY){
        this(startX, startY, endX, endY, defaultArrowHeadSize);
    }
}
 */