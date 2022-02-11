package eu.kevin.demo.main

import eu.kevin.demo.main.entities.CreditorListItem
import eu.kevin.demo.main.entities.DonationRequest

interface MainViewCallback {
    fun onCreditorSelected(creditor: CreditorListItem)
    fun onDonateClick(donationRequest: DonationRequest)
    fun openUrl(url: String)
    fun onSelectCountryClick()
    fun onAmountChanged(amount: String)
}