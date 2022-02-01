package eu.kevin.inapppayments.cardpayment

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.entities.LoadingState
import kotlinx.parcelize.Parcelize
import org.joda.money.Money

@Parcelize
internal data class CardPaymentState(
    val url: String = "",
    val amount: Money? = null,
    val showCardDetails: Boolean = true,
    val isContinueEnabled: Boolean = false,
    val loadingState: LoadingState? = null
) : IState, Parcelable