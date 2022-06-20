package eu.kevin.demo.usecases

import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.demo.auth.KevinApiClient
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.auth.entities.InitiatePaymentRequest
import eu.kevin.demo.data.database.LinkedAccountsDao

internal class InitialiseLinkedPaymentUseCase(
    private val kevinApiClient: KevinApiClient,
    private val getAccessTokenForAccountUseCase: GetAccessTokenForAccountUseCase,
    private val refreshAccessTokenUseCase: RefreshAccessTokenUseCase,
    private val linkedAccountsDao: LinkedAccountsDao
) {
    suspend fun initialiseLinkedPayment(
        initiatePaymentRequest: InitiatePaymentRequest,
        accountId: Long
    ): ApiPayment {
        val account = linkedAccountsDao.getById(accountId)
        return try {
            kevinApiClient.initializeLinkedBankPayment(
                accessToken = getAccessTokenForAccountUseCase.getAccessTokenForAccount(account!!.linkToken).accessToken,
                request = initiatePaymentRequest
            )
        } catch (e: Exception) {
            if (e is ApiError && e.statusCode == 401) {
                val refreshedAccessToken = refreshAccessTokenUseCase.refreshAccessToken(account!!.linkToken)
                kevinApiClient.initializeLinkedBankPayment(
                    accessToken = refreshedAccessToken.accessToken,
                    request = initiatePaymentRequest
                )
            } else {
                throw e
            }
        }
    }
}