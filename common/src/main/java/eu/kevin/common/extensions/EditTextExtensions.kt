package eu.kevin.common.extensions

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.setOnDoneClick(callback: () -> Unit) {
    this.setOnEditorActionListener { textView, action, keyEvent ->
        if (action == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            true
        } else {
            false
        }
    }
}

fun EditText.setOnNextClick(callback: () -> Unit) {
    this.setOnEditorActionListener { textView, action, keyEvent ->
        if (action == EditorInfo.IME_ACTION_NEXT) {
            callback.invoke()
            true
        } else {
            false
        }
    }
}