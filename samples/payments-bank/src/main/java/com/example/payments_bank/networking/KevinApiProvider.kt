package com.example.payments_bank.networking

import com.example.payments_bank.networking.api.KevinApi
import com.example.payments_bank.networking.api.KevinDataApi
import com.example.payments_bank.networking.services.KevinApiService
import com.example.payments_bank.networking.services.KevinDataApiService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object KevinApiProvider {

    private const val BASE_SAMPLE_API_URL = "https://mobile-demo.kevin.eu/api/v1/"
    private const val BASE_SAMPLE_DATA_API_URL = "https://api.getkevin.eu/demo/"

    fun provideKevinApi(): KevinApi {
        val retrofit = createRetrofit(
            baseUrl = BASE_SAMPLE_API_URL
        )
        return KevinApi(
            service = retrofit.create(KevinApiService::class.java)
        )
    }

    fun provideKevinDataApi(): KevinDataApi {
        val retrofit = createRetrofit(
            baseUrl = BASE_SAMPLE_DATA_API_URL
        )
        return KevinDataApi(
            service = retrofit.create(KevinDataApiService::class.java)
        )
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .build()

        val moshi = Moshi.Builder()
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .build()
    }
}