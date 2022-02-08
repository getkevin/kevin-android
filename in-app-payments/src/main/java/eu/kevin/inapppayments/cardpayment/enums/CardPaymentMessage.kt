package eu.kevin.inapppayments.cardpayment.enums

enum class CardPaymentMessage(val value: String) {
    SOFT_REDIRECT_MODAL("SOFT_REDIRECT_MODAL"),
    HARD_REDIRECT_MODAL("HARD_REDIRECT_MODAL"),
    CARD_PAYMENT_SUBMITTING("CARD_PAYMENT_SUBMITTING");
}