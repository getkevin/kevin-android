package eu.kevin.sample.networking.api

import eu.kevin.core.enums.KevinCountry
import eu.kevin.core.enums.KevinCountry.LITHUANIA
import eu.kevin.sample.networking.entities.payments.Creditor
import eu.kevin.sample.networking.services.KevinDataApiService

internal class KevinDataApi(
    private val service: KevinDataApiService
) {

    suspend fun fetchCreditors(country: KevinCountry): List<Creditor> {
        val iso = if (country == LITHUANIA) country.iso else "EE"
        return service.getCreditors(iso).data
    }
}