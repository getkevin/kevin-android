package eu.kevin.common.architecture

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryOwner
import eu.kevin.common.providers.SavedStateProvider

abstract class BaseFlowSession(
    lifecycleOwner: LifecycleOwner,
    registryOwner: SavedStateRegistryOwner
) {
    private val savedStateRegistry: SavedStateRegistry = registryOwner.savedStateRegistry
    private val savable = Bundle()

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            private fun onCreate() {
                savedStateRegistry.consumeRestoredStateForKey("session_state")?.let { state ->
                    savable.putAll(state)
                }
            }
        })
        savedStateRegistry.registerSavedStateProvider("session_state") {
            savable
        }
    }

    protected fun <T> savedState() = SavedStateProvider.Nullable<T>(savable)
    protected fun <T> savedState(defaultValue: T) = SavedStateProvider.NotNull(savable, defaultValue)
}