package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.interfaces.Intent
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.architecture.interfaces.State
import eu.kevin.common.architecture.interfaces.View
import eu.kevin.common.providers.SavedStateProvider
import kotlinx.coroutines.launch
import android.view.View as ViewAndroid

abstract class BaseModalFragment<S : State, I : Intent, M : BaseViewModel<S, I>> :
    BottomSheetDialogFragment(),
    Navigable {

    private val savable = Bundle()

    protected abstract val viewModel: M
    protected lateinit var contentView: View<S>

    abstract fun onCreateView(context: Context): View<S>

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
    ): ViewAndroid? {
        return onCreateView(inflater.context).also {
            contentView = it
        } as ViewAndroid
    }

    override fun onViewCreated(view: ViewAndroid, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collect {
                        contentView.render(it)
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

    protected open fun onAttached() {}

    override fun onBackPressed(): Boolean = false

    protected fun <T> savedState() = SavedStateProvider.Nullable<T>(savable)
    protected fun <T> savedState(defaultValue: T) = SavedStateProvider.NotNull(savable, defaultValue)
}