package eu.kevin.common.architecture.interfaces

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow

interface Model<S : State, I : Intent> {
    val intents: Channel<I>
    val state: StateFlow<S?>
}