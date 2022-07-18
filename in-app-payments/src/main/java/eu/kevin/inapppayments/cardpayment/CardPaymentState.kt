package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.State
import eu.kevin.common.entities.KevinAmount
import eu.kevin.common.entities.LoadingState
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CardPaymentState(
    val url: String = "",
    val amount: KevinAmount? = null,
    val showCardDetails: Boolean = true,
    val isContinueEnabled: Boolean = false,
    val loadingState: LoadingState? = null
) : State, Parcelable