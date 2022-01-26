/**
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

/**
 * Defines the various input gestures used by the graph editor
 */
public enum GraphInputGesture
{

    /**
     * Panning / moving the graph editor viewport
     */
    PAN,

    /**
     * Zooming the graph editor viewport
     */
    ZOOM,

    /**
     * Resizing graph editor elements
     */
    RESIZE,

    /**
     * Moving graph editor elements
     */
    MOVE,

    /**
     * Connecting graph editor elements
     */
    CONNECT,

    /**
     * Selecting graph editor elements
     */
    SELECT;
}
