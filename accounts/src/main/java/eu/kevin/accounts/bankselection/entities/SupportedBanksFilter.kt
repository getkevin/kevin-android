package eu.kevin.accounts.bankselection.entities

internal data class SupportedBanksFilter(
    val banks: List<String> = emptyList(),
    val showOnlyAccountLinkingSupportedBanks: Boolean = false
)