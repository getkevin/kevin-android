package eu.kevin.demo.main

import eu.kevin.demo.main.entities.CreditorListItem

interface MainViewCallback {
    fun onCreditorSelected(creditor: CreditorListItem)
    fun onProceedClick()
    fun onEmailChanged(value: String)
    fun onAmountChanged(value: String)
    fun onTermsCheckboxChanged(checked: Boolean)
    fun openUrl(url: String)
    fun onPaymentTypeSelected(position: Int)
    fun onSelectCountryClick()
}