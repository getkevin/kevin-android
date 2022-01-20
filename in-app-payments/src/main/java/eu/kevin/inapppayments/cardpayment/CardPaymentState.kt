package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CardPaymentState(
    val url: String = "",
    val showCardDetails: Boolean = true,
    val isContinueEnabled: Boolean = false,
    val loadingState: LoadingState? = null
) : IState, Parcelable