package eu.kevin.demo.screens.payment.entities

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

internal sealed class ValidationResult(@StringRes private val messageRes: Int) : Parcelable {
    @Parcelize
    object Valid : ValidationResult(0)

    @Parcelize
    data class Invalid(@StringRes val errorRes: Int) : ValidationResult(errorRes)

    fun isInvalid(): Boolean {
        return this !is Valid
    }

    fun isValid(): Boolean {
        return this is Valid
    }

    fun getMessage(context: Context, defaultMessage: String = ""): String {
        return when (this) {
            is Valid -> ""
            else -> {
                if (messageRes != 0) {
                    context.getString(messageRes)
                } else defaultMessage
            }
        }
    }
}