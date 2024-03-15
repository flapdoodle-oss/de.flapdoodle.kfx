package de.flapdoodle.kfx.controls.bettertable.events

class ReadOnlyState<T: Any> : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
        when (event) {
            is TableEvent.ResizeColumn<T, out Any> -> {
                println("TODO: not implemented right now: $event")
            }
            else -> {
                // do nothing
            }
        }
        return this
    }
}