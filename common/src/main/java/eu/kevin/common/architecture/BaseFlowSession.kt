package eu.kevin.common.architecture

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                savedStateRegistry.consumeRestoredStateForKey("session_state")?.let { state ->
                    savable.putAll(state)
                }
            }
        })
        savedStateRegistry.registerSavedStateProvider("session_state") {
            savable
        }
    }

    abstract fun handleDeepLink(uri: Uri)

    protected fun <T> savedState() = SavedStateProvider.Nullable<T>(savable)
    protected fun <T> savedState(defaultValue: T) = SavedStateProvider.NotNull(savable, defaultValue)
}