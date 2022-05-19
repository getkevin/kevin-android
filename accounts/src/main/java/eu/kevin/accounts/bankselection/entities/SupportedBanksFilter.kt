package eu.kevin.accounts.bankselection.entities

data class SupportedBanksFilter(
    val banks: List<String> = emptyList(),
    val isAccountLinkingSupported: Boolean = false
)