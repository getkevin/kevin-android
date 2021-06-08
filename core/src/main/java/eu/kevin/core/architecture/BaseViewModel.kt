package eu.kevin.core.architecture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.kevin.core.architecture.interfaces.IIntent
import eu.kevin.core.architecture.interfaces.IModel
import eu.kevin.core.architecture.interfaces.IState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : IState, I : IIntent>(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), IModel<S, I> {

    override val intents = Channel<I>(Channel.UNLIMITED)
    override val state: StateFlow<S>
        get() = _state

    private val _state: MutableStateFlow<S>

    init {
        _state = MutableStateFlow(getInitialData())
        viewModelScope.launch {
            intents.consumeAsFlow().collect { intent ->
                handleIntent(intent)
            }
        }
    }

    fun getSavedState(): S? {
        return savedStateHandle.get("saved_state")
    }

    protected abstract fun getInitialData(): S
    protected abstract suspend fun handleIntent(intent: I)

    protected suspend fun updateState(handler: suspend (intent: S) -> S) {
        _state.value = handler(state.value)
        savedStateHandle.set("saved_state", _state.value)
    }
}