package de.flapdoodle.kfx.controls.bettertable.events

class ReadOnlyState<T: Any> : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
        return this
    }
}