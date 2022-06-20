package eu.kevin.demo.screens.paymenttype

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal class PaymentTypeFragmentConfiguration(
    val linkedPaymentAvailable: Boolean
) : Parcelable