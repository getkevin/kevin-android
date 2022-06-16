package eu.kevin.demo.screens.payment

import android.os.Parcelable
import eu.kevin.common.architecture.interfaces.IState
import eu.kevin.common.entities.LoadingState
import eu.kevin.demo.screens.payment.entities.CreditorListItem
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PaymentViewState(
    val loadingState: LoadingState = LoadingState.Loading(false),
    val selectedCountry: String = "LT",
    val creditors: List<CreditorListItem> = emptyList(),
    val buttonText: String = "0.00",
    val loadingCreditors: Boolean = false
) : IState, Parcelable