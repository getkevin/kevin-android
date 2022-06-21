package eu.kevin.inapppayments.common.enums

enum class PaymentStatus(val value: String) {
    COMPLETED("completed"),
    PENDING("pending"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String?): PaymentStatus {
            return when (value) {
                COMPLETED.value -> COMPLETED
                PENDING.value -> PENDING
                else -> UNKNOWN
            }
        }
    }
}