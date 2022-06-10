package eu.kevin.demo.payment.entities

data class CreditorListItem(
    val logo: String,
    val name: String,
    val iban: String,
    val isSelected: Boolean = false
)