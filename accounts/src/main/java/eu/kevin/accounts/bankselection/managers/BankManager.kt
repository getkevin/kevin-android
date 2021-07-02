package eu.kevin.accounts.bankselection.managers

import eu.kevin.accounts.networking.entities.ApiBank

interface BankManager {
    suspend fun getSupportedBanks(country: String, authState: String): List<ApiBank>
}