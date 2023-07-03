package eu.kevin.inapppayments.enums

enum class PaymentStatus(val value: String) {
    COMPLETED("completed"),
    PENDING("pending"),
    FAILED("failed"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String?): PaymentStatus {
            return when (value) {
                COMPLETED.value -> COMPLETED
                PENDING.value -> PENDING
                FAILED.value -> FAILED
                else -> UNKNOWN
            }
        }
    }
}