package eu.kevin.common.architecture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.architecture.interfaces.IModel
import eu.kevin.common.architecture.interfaces.IState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : IState, I : IIntent>(
    protected val savedStateHandle: SavedStateHandle
) : ViewModel(), IModel<S, I> {

    override val intents = Channel<I>(Channel.UNLIMITED)

    private val _state: MutableStateFlow<S>
    override val state: StateFlow<S>
        get() = _state

    init {
        _state = MutableStateFlow(getInitialData())
        viewModelScope.launch {
            intents.consumeAsFlow().collect { intent ->
                handleIntent(intent)
            }
        }
    }

    protected fun getSavedState(): S? = savedStateHandle["saved_state"]

    protected abstract fun getInitialData(): S
    protected abstract suspend fun handleIntent(intent: I)

    protected suspend fun updateState(handler: suspend (intent: S) -> S) {
        _state.update { handler(_state.value) }
        savedStateHandle["saved_state"] = _state.value
    }
}