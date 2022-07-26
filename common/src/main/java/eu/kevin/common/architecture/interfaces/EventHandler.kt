package eu.kevin.common.architecture.interfaces

interface EventHandler<E : IEvent> {
    fun handleEvent(event: E)
}