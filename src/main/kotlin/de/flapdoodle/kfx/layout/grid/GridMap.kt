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
package de.flapdoodle.kfx.layout.grid

data class GridMap<T : Any>(
    private val map: Map<Pos, T> = emptyMap()
) {

    private val columnSet = map.keys.map { it.column }.toSet().sorted()
    private val rowSet = map.keys.map { it.row }.toSet().sorted()

    fun columns() = columnSet
    fun rows() = rowSet
    fun values() = map.values

    fun add(pos: Pos, value: T): GridMap<T> {
        return copy(map = map + (pos to value))
    }

    fun remove(value: T): GridMap<T> {
        val keysToRemove = map.filter { it.value == value }.keys
        return copy(map = map - keysToRemove)
    }

    fun <D : Any> mapColumns(allColumnRows: (Int, Collection<T>) -> D): List<D> {
        return columnSet.map { column ->
            val matchingColumns = map.filter { it.key.column == column }.values
            allColumnRows(column, matchingColumns)
        }
    }

    fun <D : Any> mapRows(allRowColumns: (Int, Collection<T>) -> D): List<D> {
        return rowSet.map { row ->
            val matchingRows = map.filter { it.key.row == row }.values
            allRowColumns(row, matchingRows)
        }
    }

    operator fun get(pos: Pos): T? {
        return map[pos]
    }

    companion object {
        fun <T : Any> create(src: Collection<T>, posOf: (T) -> Pos): GridMap<T> {
            return GridMap(src.map { posOf(it) to it }.toMap())
        }
    }

    data class Pos(
        val column: Int,
        val row: Int
    ) {
        init {
            require(column >= 0) { "invalid column: $column" }
            require(row >= 0) { "invalid row: $row" }
        }
    }
}