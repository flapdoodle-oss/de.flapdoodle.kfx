package de.flapdoodle.kfx.controls.graphmodeleditor.model

import de.flapdoodle.kfx.controls.grapheditor.slots.Position
import de.flapdoodle.kfx.controls.grapheditor.slots.Slot
import de.flapdoodle.kfx.controls.grapheditor.types.SlotId
import de.flapdoodle.kfx.controls.graphmodeleditor.types.VertexId
import de.flapdoodle.kfx.types.Id
import javafx.geometry.Point2D

data class Vertex<T>(
  val name: String,
  val data: T,
  val slots: List<Slot> = emptyList(),
  val position: Point2D = Point2D(0.0, 0.0),
  val id: VertexId<T> = VertexId()
) {

  init {
    val slotCollisions = slots.groupBy { it.id }.filter { it.value.size > 1}
    require(slotCollisions.isEmpty()) { "some slots share same id: $slotCollisions" }
  }

  private val slotMap = slots.associateBy { it.id }

  fun slotIds() = slotMap.keys
  fun slot(id: SlotId) = requireNotNull(slotMap[id]) { "could not get slot $id"}

  fun add(slot: Slot): Vertex<T> {
    return copy(slots = slots + slot)
  }

  companion object {
    private fun <T> nextId(): Id<Vertex<T>> {
      return Id.nextId(Vertex::class) as Id<Vertex<T>>
    }

    fun <T> slotChanges(old: Vertex<T>, new: Vertex<T>): Change<Slot> {
      require(old.id == new.id) {"id does not match: ${old.id} != ${new.id}"}

      val removed = (old.slotIds() - new.slotIds()).map { old.slot(it) }.toSet()
      val sameIds = old.slotIds().intersect(new.slotIds()).toSet()
      val notChanged = sameIds.filter { old.slot(it) == new.slot(it) }.map { new.slot(it) }.toSet()
      val modified = sameIds.filter { old.slot(it) != new.slot(it) }.map { old.slot(it) to new.slot(it) }.toSet()
      val added = (new.slotIds() - old.slotIds()).map { new.slot(it) }.toSet()

      return Change(
        removed = removed,
        notChanged = notChanged,
        modified = modified,
        added = added
      )
    }

  }
}