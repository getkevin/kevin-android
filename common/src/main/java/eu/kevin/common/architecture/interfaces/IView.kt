package eu.kevin.common.architecture.interfaces

interface IView<S : IState, E : IEvent> {
    fun render(state: S)
    fun handleEvent(event: E) = Unit
}