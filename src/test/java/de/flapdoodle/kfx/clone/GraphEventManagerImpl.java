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

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;


/**
 * Default implementation of {@link GraphEventManager}
 */
public class GraphEventManagerImpl implements GraphEventManager
{

    private GraphInputGesture gesture;
    private Object owner;

    @Override
    public boolean activateGesture(final GraphInputGesture pGesture, final Event pEvent, final Object pOwner)
    {
        System.out.println("activateGesture gesture: "+pGesture+", owner: "+pOwner);
        if (!canOverwrite(owner, pOwner))
        {
            System.out.println("!canOverwrite");
            return false;
        }
        if (canActivate(pGesture, pEvent))
        {
            System.out.println("canActivate");
            gesture = pGesture;
            owner = pOwner;
            return true;
        }
        // ELSE:
        return false;
    }

    private boolean canActivate(final GraphInputGesture pGesture, final Event pEvent)
    {
        final GraphInputGesture current = gesture;
        if (current == pGesture)
        {
            return true;
        }
        else if (current == null)
        {
            final boolean isTouch = pEvent instanceof TouchEvent || pEvent instanceof MouseEvent && ((MouseEvent) pEvent).isSynthesized()
                    || pEvent instanceof ScrollEvent && ((ScrollEvent) pEvent).getTouchCount() > 0;
            if (!isTouch)
            {
                switch (pGesture)
                {
                    case PAN:
                        return pEvent instanceof ScrollEvent && !((ScrollEvent) pEvent).isControlDown()
                                || pEvent instanceof MouseEvent && ((MouseEvent) pEvent).isSecondaryButtonDown();

                    case ZOOM:
                        return pEvent instanceof ScrollEvent && ((ScrollEvent) pEvent).isControlDown();

                    case SELECT:
                    case CONNECT:
                    case MOVE:
                    case RESIZE:
                        return pEvent instanceof MouseEvent && ((MouseEvent) pEvent).isPrimaryButtonDown();
                }
            }
            else
            {
                switch (pGesture)
                {
                    case ZOOM:
                        return pEvent instanceof ZoomEvent;

                    case PAN:
                        return pEvent instanceof TouchEvent && ((TouchEvent) pEvent).getTouchCount() > 1;

                    case SELECT:
                    case CONNECT:
                    case MOVE:
                    case RESIZE:
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean finishGesture(final GraphInputGesture pExpected, final Object pOwner)
    {
        if (gesture == pExpected && (owner == pOwner || !isVisible(owner)))
        {
            gesture = null;
            owner = null;
            return true;
        }
        return false;
    }

    private static boolean canOverwrite(final Object pExisting, final Object pCandidate)
    {
        if (pExisting == pCandidate)
        {
            return true;
        }
        if (pCandidate == null)
        {
            return false;
        }
        return pExisting == null || !isVisible(pExisting);
    }

    private static boolean isVisible(final Object pNode)
    {
        if (pNode != null)
        {
            if (pNode instanceof Node)
            {
                return ((Node) pNode).isVisible() && ((Node) pNode).getParent() != null && ((Node) pNode).getScene() != null;
            }
            return true;
        }
        // ELSE: null
        return false;
    }
}
