package eu.kevin.inapppayments.networking

import eu.kevin.accounts.networking.entities.ApiBank
import eu.kevin.core.networking.BaseApiClient
import eu.kevin.inapppayments.networking.entities.CardPaymentInfo

interface KevinPaymentsClient : BaseApiClient {

    /**
     * @param paymentId paymentId
     *
     * @return [CardPaymentInfo] containing info about payment
     */
    suspend fun getCardPaymentInfo(paymentId: String): CardPaymentInfo

    /**
     * @param paymentId paymentId
     * @param cardNumberPart part of card number to get bank for(needs at least first 6 digits)
     *
     * @return [ApiBank] returns bank object for this card number or error with code 20013 if
     * bank couldn't be found
     */
    suspend fun getBankFromCardNumber(paymentId: String, cardNumberPart: String): ApiBank
}