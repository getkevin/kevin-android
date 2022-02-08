package eu.kevin.inapppayments.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.inapppayments.networking.entities.CardPaymentInfo
import io.ktor.client.*
import io.ktor.client.request.*

internal class KevinPaymentsApiClient(
    private val httpClient: HttpClient
) : KevinPaymentsClient {

    override suspend fun getCardPaymentInfo(paymentId: String): CardPaymentInfo {
        return httpClient.get("platform/frame/card/payment/${paymentId}")
    }

    override suspend fun getBankFromCardNumber(paymentId: String, cardNumberPart: String): ApiBank {
        return httpClient.get("platform/frame/banks/cards/${paymentId}/${cardNumberPart}")
    }
}