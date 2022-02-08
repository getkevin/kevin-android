package eu.kevin.inapppayments.cardpaymentredirect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardPaymentRedirectFragmentConfiguration(
    val bankName: String?
) : Parcelable