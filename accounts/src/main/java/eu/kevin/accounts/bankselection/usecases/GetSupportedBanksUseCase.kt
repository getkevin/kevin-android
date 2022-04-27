package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.managers.BankManagerInterface
import eu.kevin.accounts.networking.entities.ApiBank

internal class GetSupportedBanksUseCase(
    private val bankManager: BankManagerInterface
) {
    suspend fun getSupportedBanks(country: String, authState: String, filter: List<String>): List<ApiBank> {
        val apiBanks = bankManager.getSupportedBanks(country, authState)
        return if (filter.isNotEmpty()) {
            apiBanks.filter {
                filter.contains(it.id.lowercase())
            }
        } else {
            apiBanks
        }
    }
}