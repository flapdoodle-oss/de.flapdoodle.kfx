package de.flapdoodle.kfx.collections

data class Change<T>(
    val removed: Set<T>,
    val notChanged: Set<T>,
    val modified: Set<Pair<T,T>>,
    val added: Set<T>
) {
    companion object {
        fun <T> empty(): Change<T> {
            return Change(emptySet(), emptySet(), emptySet(), emptySet())
        }
    }
}