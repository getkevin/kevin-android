package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.networking.KevinAccountsClient
import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.common.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.withContext

class ValidateBanksConfigUseCase(
    private val dispatchers: CoroutineDispatchers,
    private val accountsClient: KevinAccountsClient
) {

    suspend fun validateBanksConfig(
        authState: String,
        country: String?,
        preselectedBank: String?,
        banksFilter: List<String>,
        requireAccountLinkingSupport: Boolean
    ): Status {
        return withContext(dispatchers.io) {
            val apiBanks = accountsClient.getSupportedBanks(authState, country).data
                .let {
                    if (requireAccountLinkingSupport) {
                        it.filter { it.isAccountLinkingSupported }
                    } else {
                        it
                    }
                }

            var validFilters = apiBanks
            if (banksFilter.isNotEmpty()) {
                validFilters = banksFilter.mapNotNull { bank ->
                    apiBanks.find { it.id.equals(bank, true) }
                }

                if (validFilters.isEmpty()) {
                    return@withContext Status.FiltersInvalid
                }
            }

            var selectedBank: ApiBank? = null
            if (!preselectedBank.isNullOrBlank()) {
                selectedBank = validFilters.find { it.id.equals(preselectedBank, true) }

                if (selectedBank == null) {
                    return@withContext Status.PreselectedInvalid
                }
            }

            return@withContext Status.Valid(selectedBank)
        }
    }

    sealed class Status {
        data class Valid(val selectedBank: ApiBank?) : Status()
        object PreselectedInvalid : Status()
        object FiltersInvalid : Status()
    }
}