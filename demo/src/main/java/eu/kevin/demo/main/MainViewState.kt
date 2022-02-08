package eu.kevin.demo.main

import eu.kevin.common.entities.LoadingState
import eu.kevin.demo.main.entities.CreditorListItem

data class MainViewState(
    val loadingState: LoadingState = LoadingState.Loading(false),
    val countries: List<String> = emptyList(),
    val selectedCountry: String = "LT",
    val creditors: List<CreditorListItem> = emptyList(),
    val proceedButtonEnabled: Boolean = false,
    val buttonText: String = "",
    val loadingCreditors: Boolean = false,
    val emailError: String? = null,
    val amountError: String? = null,
    val termsError: Boolean = false
)