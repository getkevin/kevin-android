package eu.kevin.common.architecture

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding

abstract class BaseView<T : ViewBinding>(context: Context) : ConstraintLayout(context) {

    protected abstract var binding: T?

    protected fun requireBinding(): T {
        return binding ?: throw IllegalStateException("Binding is not set.")
    }

    open fun onAttached() {}

    fun onDestroyView() {
        binding = null
    }
}