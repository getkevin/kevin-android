package com.example.account_linking.networking

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object KevinApiProvider {

    private const val BASE_SAMPLE_API_URL = "https://mobile-demo.kevin.eu/api/v1/"

    fun provideKevinApi(): KevinApi {
        val retrofit = createRetrofit()
        return KevinApi(
            service = retrofit.create(KevinApiService::class.java)
        )
    }

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            // Adding OkHttp logging interceptor so network traffic can be monitored via Logcat.
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                })
            .build()

        val moshi = Moshi.Builder()
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_SAMPLE_API_URL)
            .build()
    }
}