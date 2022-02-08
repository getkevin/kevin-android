package eu.kevin.common.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getInputText(): String {
    return this.editText?.text?.toString() ?: ""
}