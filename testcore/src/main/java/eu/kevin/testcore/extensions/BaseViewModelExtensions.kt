package eu.kevin.testcore.extensions

import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.interfaces.IEvent
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.coroutines.flow.MutableStateFlow

fun <S : IState, I : IIntent, E: IEvent> BaseViewModel<S, I, E>.updateInternalState(newState: S) {
    val field = BaseViewModel::class.java.getDeclaredField("_state")
    field.isAccessible = true
    field.set(this, MutableStateFlow(newState))
}