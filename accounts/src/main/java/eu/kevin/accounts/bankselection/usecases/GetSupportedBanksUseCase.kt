package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.managers.BankManagerInterface
import eu.kevin.accounts.networking.entities.ApiBank

internal class GetSupportedBanksUseCase(
    private val bankManager: BankManagerInterface
) {
    suspend fun getSupportedBanks(
        country: String,
        authState: String,
        filter: List<String>,
        keepAccountLinkingNotSupportedBanks: Boolean
    ): List<ApiBank> {
        val apiBanks = bankManager.getSupportedBanks(country, authState)
        val filteredByName = if (filter.isNotEmpty()) {
            apiBanks.filter {
                filter.contains(it.id.lowercase())
            }
        } else {
            apiBanks
        }
        return if (keepAccountLinkingNotSupportedBanks) {
            filteredByName
        } else {
            filteredByName.filter { it.isAccountLinkingSupported }
        }
    }
}