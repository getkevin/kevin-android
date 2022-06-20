package eu.kevin.demo.screens.payment

import eu.kevin.demo.screens.payment.entities.CreditorListItem
import eu.kevin.demo.screens.payment.entities.DonationRequest

internal interface PaymentViewCallback {
    fun onCreditorSelected(creditor: CreditorListItem)
    fun onDonateClick(donationRequest: DonationRequest)
    fun openUrl(url: String)
    fun onSelectCountryClick()
    fun onAmountChanged(amount: String)
}