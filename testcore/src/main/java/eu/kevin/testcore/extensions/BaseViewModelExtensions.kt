package eu.kevin.testcore.extensions

import eu.kevin.core.architecture.BaseViewModel
import eu.kevin.core.architecture.interfaces.IIntent
import eu.kevin.core.architecture.interfaces.IState
import kotlinx.coroutines.flow.MutableStateFlow

fun <S: IState, I: IIntent>BaseViewModel<S, I>.updateInternalState(newState: S) {
    val field = BaseViewModel::class.java.getDeclaredField("_state")
    field.isAccessible = true
    field.set(this, MutableStateFlow(newState))
}