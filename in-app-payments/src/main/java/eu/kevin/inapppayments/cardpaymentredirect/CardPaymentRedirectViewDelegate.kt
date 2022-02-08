package eu.kevin.inapppayments.cardpaymentredirect

internal interface CardPaymentRedirectViewDelegate {
    fun onUserConfirmed()
    fun onUserDeclined()
}