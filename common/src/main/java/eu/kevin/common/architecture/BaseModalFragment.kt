package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.interfaces.Intent
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.architecture.interfaces.State
import eu.kevin.common.architecture.interfaces.View
import eu.kevin.common.providers.SavedStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

abstract class BaseModalFragment<S : State, I : Intent, M : BaseViewModel<S, I>> :
    BottomSheetDialogFragment(),
    CoroutineScope,
    Navigable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private val savable = Bundle()

    protected abstract val viewModel: M
    protected lateinit var contentView: eu.kevin.common.architecture.interfaces.View<S>

    abstract fun onCreateView(context: Context): eu.kevin.common.architecture.interfaces.View<S>

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savable.putAll(savedInstanceState.getBundle("_state"))
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        job = SupervisorJob()
        observeChanges()
        return onCreateView(inflater.context).also {
            contentView = it
        } as View
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        onAttached()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle("_state", savable)
        super.onSaveInstanceState(outState)
    }

    protected open fun onAttached() {}

    override fun onBackPressed(): Boolean {
        return false
    }

    private fun observeChanges() {
        viewModel.state.onEach {
            contentView.render(it)
        }.launchIn(this)
    }

    protected fun <T> savedState() = SavedStateProvider.Nullable<T>(savable)
    protected fun <T> savedState(defaultValue: T) = SavedStateProvider.NotNull(savable, defaultValue)
}