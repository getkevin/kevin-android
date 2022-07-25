package eu.kevin.common.architecture.interfaces

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IModel<S : IState, I : IIntent, E : IEvent> {
    val state: StateFlow<S?>
    val intents: Channel<I>
    val events: Flow<E>
}