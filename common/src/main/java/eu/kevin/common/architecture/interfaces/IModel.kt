package eu.kevin.common.architecture.interfaces

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface IModel<S : IState, I : IIntent> {
    val state: StateFlow<S?>
    val intents: Channel<I>
}