package eu.kevin.accounts.bankselection.managers

import eu.kevin.accounts.networking.KevinAccountsClient
import eu.kevin.accounts.networking.entities.ApiBank

class KevinBankManager(
    private val kevinAccountsClient: KevinAccountsClient
) : BankManagerInterface {
    override suspend fun getSupportedBanks(country: String?, authState: String): List<ApiBank> {
        return kevinAccountsClient.getSupportedBanks(authState, country).data
    }
}