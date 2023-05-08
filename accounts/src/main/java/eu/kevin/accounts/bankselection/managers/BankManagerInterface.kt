package eu.kevin.accounts.bankselection.managers

import eu.kevin.accounts.networking.entities.ApiBank

interface BankManagerInterface {
    suspend fun getSupportedBanks(country: String?, authState: String): List<ApiBank>
}