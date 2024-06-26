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
package de.flapdoodle.kfx.clone;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;


/**
 * An arrow-head shape.
 *
 * <p>
 * This is used by the {@link Arrow} class.
 * </p>
 */
public class ArrowHead extends Path
{

    private static final double DEFAULT_LENGTH = 10;
    private static final double DEFAULT_WIDTH = 10;

    private double x;
    private double y;
    private double length = DEFAULT_LENGTH;
    private double width = DEFAULT_WIDTH;
    private double radius = -1;

    private final Rotate rotate = new Rotate();

    /**
     * Creates a new {@link ArrowHead}.
     */
    public ArrowHead()
    {
        setFill(Color.BLACK);
        setStrokeType(StrokeType.INSIDE);
        getTransforms().add(rotate);
    }

    /**
     * Sets the center position of the arrow-head.
     *
     * @param pX
     *            the x-coordinate of the center of the arrow-head
     * @param pY
     *            the y-coordinate of the center of the arrow-head
     */
    public void setCenter(final double pX, final double pY)
    {
        x = pX;
        y = pY;

        rotate.setPivotX(pX);
        rotate.setPivotY(pY);
    }

    /**
     * Sets the length of the arrow-head.
     *
     * @param pLength
     *            the length of the arrow-head
     */
    public void setLength(final double pLength)
    {
        length = pLength;
    }

    /**
     * Gets the length of the arrow-head.
     *
     * @return the length of the arrow-head
     */
    public double getLength()
    {
        return length;
    }

    /**
     * Sets the width of the arrow-head.
     *
     * @param pWidth
     *            the width of the arrow-head
     */
    public void setWidth(final double pWidth)
    {
        width = pWidth;
    }

    /**
     * Sets the radius of curvature of the {@link ArcTo} at the base of the
     * arrow-head.
     *
     * <p>
     * If this value is less than or equal to zero, a straight line will be
     * drawn instead. The default is -1.
     * </p>
     *
     * @param pRadius
     *            the radius of curvature of the arc at the base of the
     *            arrow-head
     */
    public void setRadiusOfCurvature(final double pRadius)
    {
        radius = pRadius;
    }

    /**
     * Sets the rotation angle of the arrow-head.
     *
     * @param angle
     *            the rotation angle of the arrow-head, in degrees
     */
    public void setAngle(final double angle)
    {
        rotate.setAngle(angle);
    }

    /**
     * Draws the arrow-head for its current size and position values.
     */
    public void draw()
    {
        getElements().clear();

        getElements().add(new MoveTo(x, y + length / 2));
        getElements().add(new LineTo(x + width / 2, y - length / 2));

        if (radius > 0)
        {
            final ArcTo arcTo = new ArcTo();
            arcTo.setX(x - width / 2);
            arcTo.setY(y - length / 2);
            arcTo.setRadiusX(radius);
            arcTo.setRadiusY(radius);
            arcTo.setSweepFlag(true);
            getElements().add(arcTo);
        }
        else
        {
            getElements().add(new LineTo(x - width / 2, y - length / 2));
        }

        getElements().add(new ClosePath());
    }
}
