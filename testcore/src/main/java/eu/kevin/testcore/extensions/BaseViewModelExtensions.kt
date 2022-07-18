package eu.kevin.testcore.extensions

import eu.kevin.common.architecture.BaseViewModel
import eu.kevin.common.architecture.interfaces.Intent
import eu.kevin.common.architecture.interfaces.State
import kotlinx.coroutines.flow.MutableStateFlow

fun <S : State, I : Intent> BaseViewModel<S, I>.updateInternalState(newState: S) {
    val field = BaseViewModel::class.java.getDeclaredField("_state")
    field.isAccessible = true
    field.set(this, MutableStateFlow(newState))
}