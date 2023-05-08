package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.bankselection.managers.BankManagerInterface
import eu.kevin.accounts.networking.entities.ApiBank

class GetSupportedBanksUseCase(
    private val bankManager: BankManagerInterface
) {
    suspend fun getSupportedBanks(
        country: String?,
        authState: String,
        supportedBanksFilter: SupportedBanksFilter = SupportedBanksFilter()
    ): List<ApiBank> {
        val apiBanks = bankManager.getSupportedBanks(country, authState)
        val filteredByName = if (supportedBanksFilter.banks.isNotEmpty()) {
            apiBanks.filter {
                supportedBanksFilter.banks.contains(it.id.lowercase())
            }
        } else {
            apiBanks
        }
        return if (supportedBanksFilter.showOnlyAccountLinkingSupportedBanks) {
            filteredByName.filter { it.isAccountLinkingSupported }
        } else {
            filteredByName
        }
    }
}