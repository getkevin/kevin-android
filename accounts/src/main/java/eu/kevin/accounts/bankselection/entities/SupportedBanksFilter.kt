package eu.kevin.accounts.bankselection.entities

data class SupportedBanksFilter(
    val banks: List<String> = emptyList(),
    val showOnlyAccountLinkingSupportedBanks: Boolean = false
)