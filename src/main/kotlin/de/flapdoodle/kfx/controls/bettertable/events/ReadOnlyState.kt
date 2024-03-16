package de.flapdoodle.kfx.controls.bettertable.events

class ReadOnlyState<T: Any>(
    private val context: EventContext<T>
) : State<T> {
    override fun onEvent(event: TableEvent.RequestEvent<T>): State<T> {
        when (event) {
            is TableEvent.RequestResizeColumn<T, out Any> -> {
                context.onTableEvent(event.ok())
            }
            else -> {
                // do nothing
            }
        }
        return this
    }
}