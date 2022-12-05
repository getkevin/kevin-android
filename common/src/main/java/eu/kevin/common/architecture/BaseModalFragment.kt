package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.providers.SavedStateProvider
import kotlinx.coroutines.launch

abstract class BaseModalFragment<S : IState, I : IIntent, M : BaseViewModel<S, I>> :
    BottomSheetDialogFragment(),
    Navigable {

    private val savable = Bundle()

    protected abstract val viewModel: M
    private var contentView: IView<S>? = null

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
        return onCreateView(inflater.context).also {
            contentView = it
        } as View
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collect {
                        contentView?.render(it)
                    }
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        onAttached()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBundle("_state", savable)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        (contentView as? BaseView<*>)?.onDestroyView()
        contentView = null
        super.onDestroyView()
    }

    protected open fun onAttached() {}

    override fun onBackPressed(): Boolean = false

    protected fun <T> savedState() = SavedStateProvider.Nullable<T>(savable)
    protected fun <T> savedState(defaultValue: T) = SavedStateProvider.NotNull(savable, defaultValue)
}