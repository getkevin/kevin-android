package eu.kevin.core.architecture.interfaces

interface IView<S: IState> {
    fun render(state: S)
}