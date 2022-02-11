package eu.kevin.demo.main.factories

import eu.kevin.demo.data.entities.Creditor
import eu.kevin.demo.main.entities.CreditorListItem

internal object CreditorsListFactory {

    fun getCreditorsList(creditors: List<Creditor>) =
        creditors.map {
            CreditorListItem(
                logo = it.logo,
                name = it.name,
                iban = it.iban,
                isSelected = false
            )
        }
}