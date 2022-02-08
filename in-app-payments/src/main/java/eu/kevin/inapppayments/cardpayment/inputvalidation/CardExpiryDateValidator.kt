package eu.kevin.inapppayments.cardpayment.inputvalidation

import android.annotation.SuppressLint
import eu.kevin.inapppayments.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

internal object CardExpiryDateValidator {
    private val expiryDatePattern = Pattern.compile("^(0[1-9]|1[0-2])/?([0-9]{4}|[0-9]{2})$")

    fun validate(expiryDate: String): ValidationResult {
        return when {
            expiryDate.isBlank() -> ValidationResult.Invalid(R.string.error_no_expiry_date)
            !expiryDatePattern.matcher(expiryDate).matches() -> ValidationResult.Invalid(R.string.error_invalid_expiry_date)
            !checkIfNotExpired(expiryDate) -> ValidationResult.Invalid(R.string.error_card_expired)
            else -> ValidationResult.Valid
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkIfNotExpired(expiryDate: String): Boolean {
        val currentTime = Calendar.getInstance()
        val expirationDate = Calendar.getInstance().apply {
            time = SimpleDateFormat("MM/yy").parse(expiryDate)!!
        }
        return expirationDate.get(Calendar.MONTH) >= currentTime.get(Calendar.MONTH)
            && expirationDate.get(Calendar.YEAR) >= currentTime.get(Calendar.YEAR)
    }
}