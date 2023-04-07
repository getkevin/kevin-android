package com.example.payments_card.networking.api

import com.example.payments_card.networking.entities.Creditor
import com.example.payments_card.networking.services.KevinDataApiService
import eu.kevin.core.enums.KevinCountry
import eu.kevin.core.enums.KevinCountry.LITHUANIA

internal class KevinDataApi(
    private val service: KevinDataApiService
) {

    suspend fun fetchCreditors(country: KevinCountry): List<Creditor> {
        val iso = if (country == LITHUANIA) country.iso else "EE"
        return service.getCreditors(iso).data
    }
}