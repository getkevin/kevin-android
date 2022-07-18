package eu.kevin.common.architecture.interfaces

interface View<S : State> {
    fun render(state: S)
}