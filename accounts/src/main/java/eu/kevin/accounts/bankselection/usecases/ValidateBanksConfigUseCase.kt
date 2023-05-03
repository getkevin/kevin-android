package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.networking.KevinAccountsClient
import eu.kevin.accounts.networking.entities.ApiBank

class ValidateBanksConfigUseCase(
    private val accountsClient: KevinAccountsClient
) {

    suspend fun validateBanksConfig(
        authState: String,
        country: String?,
        preselectedBank: String?,
        banksFilter: List<String>,
        requireAccountLinkingSupport: Boolean
    ): Status {
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
                return Status.FiltersInvalid
            }
        }

        var selectedBank: ApiBank? = null
        if (!preselectedBank.isNullOrBlank()) {
            selectedBank = validFilters.find { it.id.equals(preselectedBank, true) }

            if (selectedBank == null) {
                return Status.PreselectedInvalid
            }
        }

        return Status.Valid(selectedBank)
    }

    sealed class Status {
        data class Valid(val selectedBank: ApiBank?) : Status()
        object PreselectedInvalid : Status()
        object FiltersInvalid : Status()
    }
}