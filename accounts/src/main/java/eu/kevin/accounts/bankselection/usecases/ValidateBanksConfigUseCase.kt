package eu.kevin.accounts.bankselection.usecases

import eu.kevin.accounts.bankselection.entities.SupportedBanksFilter
import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.common.dispatchers.CoroutineDispatchers
import kotlinx.coroutines.withContext

class ValidateBanksConfigUseCase(
    private val dispatchers: CoroutineDispatchers,
    private val getSupportedBanksUseCase: GetSupportedBanksUseCase
) {

    suspend fun validateBanksConfig(
        authState: String,
        country: String?,
        preselectedBank: String?,
        supportedBanksFilter: SupportedBanksFilter
    ): Status {
        return withContext(dispatchers.io) {
            val supportedBanks = getSupportedBanksUseCase.getSupportedBanks(
                country = country,
                authState = authState,
                supportedBanksFilter = supportedBanksFilter
            )

            if (supportedBanksFilter.banks.isNotEmpty() && supportedBanks.isEmpty()) {
                return@withContext Status.FiltersInvalid
            }

            var selectedBank: ApiBank? = null
            if (!preselectedBank.isNullOrBlank()) {
                selectedBank = supportedBanks.find { it.id.equals(preselectedBank, true) }

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