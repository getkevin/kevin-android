package eu.kevin.demo.screens.paymenttype

interface PaymentTypeViewCallback {
    fun onBankPaymentSelected()
    fun onLinkedPaymentSelected()
    fun onCardPaymentSelected()
}