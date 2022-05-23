package eu.kevin.common.architecture.interfaces

interface IView<S : IState> {
    fun render(state: S)
}