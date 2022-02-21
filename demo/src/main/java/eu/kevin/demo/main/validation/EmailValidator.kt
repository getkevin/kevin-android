package eu.kevin.demo.main.validation

import android.util.Patterns
import eu.kevin.demo.R
import eu.kevin.demo.main.entities.ValidationResult

internal object EmailValidator {

    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(R.string.window_main_email_blank_error)
            !email.isValidEmail() -> ValidationResult.Invalid(R.string.window_main_email_invalid_format_error)
            else -> ValidationResult.Valid
        }
    }

    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}