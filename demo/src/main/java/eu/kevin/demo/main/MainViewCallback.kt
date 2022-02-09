package eu.kevin.demo.main

import eu.kevin.demo.main.entities.CreditorListItem

interface MainViewCallback {
    fun onCreditorSelected(creditor: CreditorListItem)
    fun onProceedClick(
        email: String,
        amount: String,
        termsAccepted: Boolean
    )
    fun openUrl(url: String)
    fun onPaymentTypeSelected(position: Int)
    fun onSelectCountryClick()
    fun onAmountChanged(amount: String)
}