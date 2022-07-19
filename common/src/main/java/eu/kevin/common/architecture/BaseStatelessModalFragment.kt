package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.providers.SavedStateProvider

abstract class BaseStatelessModalFragment :
    BottomSheetDialogFragment(),
    Navigable {

    private val savable = Bundle()

    abstract fun onCreateView(context: Context): View

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
        return onCreateView(inflater.context)
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