package eu.kevin.demo.screens.paymenttype

internal interface PaymentTypeViewCallback {
    fun onBankPaymentSelected()
    fun onLinkedPaymentSelected()
    fun onCardPaymentSelected()
}