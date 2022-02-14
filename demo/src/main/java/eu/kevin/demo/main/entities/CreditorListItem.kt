package eu.kevin.demo.main.entities

import eu.kevin.demo.data.entities.Creditor

data class CreditorListItem(
    val logo: String,
    val name: String,
    val iban: String,
    val isSelected: Boolean = false
)