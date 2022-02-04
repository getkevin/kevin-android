package eu.kevin.demo.main

import eu.kevin.demo.main.entities.CreditorListItem

data class MainViewState(
    val isLoading: Boolean = false,
    val countries: List<String> = emptyList(),
    val selectedCountry: String = "LT",
    val creditors: List<CreditorListItem> = emptyList(),
    val proceedButtonEnabled: Boolean = false,
    val buttonText: String = "",
    val loadingCreditors: Boolean = false
)