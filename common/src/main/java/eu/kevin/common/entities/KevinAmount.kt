package eu.kevin.common.entities

import android.content.Context
import android.os.Parcelable
import eu.kevin.common.extensions.getCurrentLocale
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.util.*

@Parcelize
data class KevinAmount(
    val amount: Double,
    val currency: Currency
) : Parcelable {
    fun getDisplayString(context: Context): String {
        val formatter = NumberFormat.getCurrencyInstance(context.getCurrentLocale())
        formatter.currency = currency
        return formatter.format(amount)
    }
}