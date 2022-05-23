package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.providers.SavedStateProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<S : IState, I : IIntent, M : BaseViewModel<S, I>> :
    Fragment(),
    CoroutineScope,
    Navigable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    private val savable = Bundle()

    protected abstract val viewModel: M
    protected lateinit var contentView: IView<S>

    abstract fun onCreateView(context: Context): IView<S>

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