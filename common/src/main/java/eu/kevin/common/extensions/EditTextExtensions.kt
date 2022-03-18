package eu.kevin.common.extensions

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.setOnDoneActionListener(callback: () -> Unit) {
    this.setOnEditorActionListener { _, action, _ ->
        if (action == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        } else {
            false
        }
    }
}

fun EditText.setOnNextActionListener(callback: () -> Unit) {
    this.setOnEditorActionListener { _, action, _ ->
        if (action == EditorInfo.IME_ACTION_NEXT) {
            callback.invoke()
            true
        } else {
            false
        }
    }
}