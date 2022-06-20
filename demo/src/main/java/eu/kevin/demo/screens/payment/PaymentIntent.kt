package eu.kevin.demo.screens.payment

import eu.kevin.common.architecture.interfaces.IIntent
import eu.kevin.core.entities.SessionResult
import eu.kevin.demo.screens.payment.entities.CreditorListItem
import eu.kevin.demo.screens.payment.entities.DonationRequest
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType
import eu.kevin.inapppayments.paymentsession.PaymentSessionResult

internal sealed class PaymentIntent : IIntent {
    data class OnCreditorSelected(val creditor: CreditorListItem) : PaymentIntent()
    data class OnCountrySelected(val iso: String) : PaymentIntent()
    data class OnPaymentTypeSelected(val paymentType: DemoPaymentType) : PaymentIntent()
    data class OnAccountSelected(val id: Long) : PaymentIntent()
    object OnOpenCountrySelection : PaymentIntent()
    data class OnAmountChanged(val amount: String) : PaymentIntent()
    data class OnDonationRequest(val donationRequest: DonationRequest) : PaymentIntent()
    data class OnPaymentResult(val result: SessionResult<PaymentSessionResult>) : PaymentIntent()
}