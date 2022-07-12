package eu.kevin.demo.screens.payment.factories

import eu.kevin.demo.data.entities.Creditor
import eu.kevin.demo.screens.payment.entities.CreditorListItem

internal object CreditorsListFactory {

    fun getCreditorsList(creditors: List<Creditor>) =
        creditors.map {
            CreditorListItem(
                logo = it.logo,
                name = it.name,
                iban = it.accounts.first().iban,
                isSelected = false
            )
        }
}