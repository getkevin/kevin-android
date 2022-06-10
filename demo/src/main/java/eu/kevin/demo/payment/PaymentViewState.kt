package eu.kevin.demo.payment

import eu.kevin.common.entities.LoadingState
import eu.kevin.demo.payment.entities.CreditorListItem

data class PaymentViewState(
    val loadingState: LoadingState = LoadingState.Loading(false),
    val selectedCountry: String = "LT",
    val creditors: List<CreditorListItem> = emptyList(),
    val buttonText: String = "0.00",
    val loadingCreditors: Boolean = false
)