package eu.kevin.demo.screens.payment.usecases

import eu.kevin.demo.data.KevinDataClient
import eu.kevin.demo.data.entities.Creditor

internal class GetCreditorsUseCase constructor(
    private val kevinDataClient: KevinDataClient
) {
    suspend fun getCreditors(countryIso: String): List<Creditor> {
        return kevinDataClient.getCreditorsByCountry(
            if (countryIso != "LT") "EE" else countryIso
        ).data
    }
}