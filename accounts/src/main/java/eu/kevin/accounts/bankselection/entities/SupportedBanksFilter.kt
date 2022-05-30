package eu.kevin.accounts.bankselection.entities

internal data class SupportedBanksFilter(
    val banks: List<String> = emptyList(),
    val isAccountLinking: Boolean = false
)