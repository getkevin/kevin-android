package eu.kevin.demo.screens.payment.entities

internal data class CreditorListItem(
    val logo: String,
    val name: String,
    val iban: String,
    val isSelected: Boolean = false
)