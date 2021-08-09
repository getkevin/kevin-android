package eu.kevin.common.architecture

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding

abstract class BaseView<T : ViewBinding>(context: Context) : ConstraintLayout(context) {

    protected abstract val binding: T

    open fun onAttached() {}
}