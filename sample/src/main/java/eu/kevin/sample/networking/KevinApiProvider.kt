package eu.kevin.sample.networking

import com.squareup.moshi.Moshi
import eu.kevin.sample.networking.api.KevinApi
import eu.kevin.sample.networking.api.KevinDataApi
import eu.kevin.sample.networking.services.KevinApiService
import eu.kevin.sample.networking.services.KevinDataApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
            .baseUrl(baseUrl)
            .build()
    }
}