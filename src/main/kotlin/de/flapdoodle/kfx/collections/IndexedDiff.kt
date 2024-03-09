package de.flapdoodle.kfx.collections

object IndexedDiff {
  fun <T> changes(old: List<T>, new: List<T>): List<Change<T>> {
    var changes = emptyList<Change<T>>()

    val oldAsSet = old.toSet()
    val newAsSet = new.toSet()

    val removed = oldAsSet - newAsSet
    val added = newAsSet - oldAsSet
    val stillThere = oldAsSet.intersect(newAsSet)

    changes = changes + old.flatMapIndexed { index: Int, t: T -> if (removed.contains(t)) listOf(Change.Remove(index)) else emptyList() }
    changes = changes + moves(old.filter { !removed.contains(it) }, new, stillThere)

    return changes
  }

  private fun <T> moves(old: List<T>, new: List<T>, stillThere: Set<T>): List<Change<T>> {
    val oldIndexMap = old.flatMapIndexed {
      index: Int, t: T -> if (stillThere.contains(t)) listOf(t to index) else emptyList()
    }.groupBy(Pair<T, Int>::first) { it.second }

    val newIndexMap = new.flatMapIndexed {
        index: Int, t: T -> if (stillThere.contains(t)) listOf(t to index) else emptyList()
    }.groupBy(Pair<T, Int>::first) { it.second }

    var movementMap = stillThere.map {
      val oldPositions = oldIndexMap[it]!!
      val newPositions = newIndexMap[it]!!
      it to Movement(oldPositions, newPositions)
    }.toMap()

    val moves = old.flatMap {
      val movement = movementMap[it]
      if (movement != null) {
        val (m, change) = movement.nextChange(it)
        movementMap = movementMap - it + (it to m)
        if (change is Change.Move && change.source == change.destination) {
          emptyList()
        } else
          listOf(change)
      } else emptyList()
    }

    val valuesNotAdded = movementMap.filter { !it.value.sources.isEmpty() }
    require(valuesNotAdded.isEmpty()) {"still unprocessed changes: $valuesNotAdded"}

    val movementsLeft = movementMap.filter { !it.value.isEmpty() }
    val addThese = movementsLeft.flatMap { it.value.destinations.map { index -> index to it.key } }
      .sortedBy { it.first }

    return moves + addThese.map { Change.Add(it.first, it.second) }
  }

  data class Movement(val sources: List<Int>, val destinations: List<Int>) {
    fun isEmpty() = sources.isEmpty() && destinations.isEmpty()

    fun <T> nextChange(it: T): Pair<Movement, Change<T>> {
      return if (sources.isNotEmpty() && destinations.isNotEmpty()) {
        copy(sources = sources.subList(1, sources.size), destinations = destinations.subList(1, destinations.size)) to Change.Move(sources[0], destinations[0])
      } else {
        if (sources.size < destinations.size) {
          copy(sources = sources, destinations = destinations.subList(1, destinations.size)) to Change.Add(destinations[0], it)
        } else {
          copy(sources = sources.subList(1, sources.size), destinations = destinations) to Change.Remove(sources[0])
        }
      }
    }
  }

  sealed class Change<T> {
    data class Remove<T>(val index: Int) : Change<T>()
    data class Move<T>(val source: Int, val destination: Int) : Change<T>()
    data class Add<T>(val index: Int, val value: T) : Change<T>()
  }
}