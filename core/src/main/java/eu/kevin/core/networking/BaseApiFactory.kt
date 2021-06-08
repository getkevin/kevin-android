package eu.kevin.core.networking

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import eu.kevin.core.networking.interceptors.AuthorizationInterceptor
import eu.kevin.core.networking.interceptors.UserAgentInterceptor
import eu.kevin.core.networking.serializers.DateSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseApiFactory<T : BaseApiClient>(
    private val baseUrl: String,
    private val userAgent: String,
    private val timeout: Long? = null,
    private val httpLoggingInterceptorLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC
) {
    abstract fun createClient(tokenDelegate: TokenDelegate?): T

    protected fun createRetrofit(tokenDelegate: TokenDelegate? = null): Retrofit {
        return with(Retrofit.Builder()) {
            baseUrl(baseUrl)
            addConverterFactory(createConverterFactory())
            client(createOkHttpClient(tokenDelegate))
            build()
        }
    }

    protected open fun createConverterFactory(): Converter.Factory {
        return Json {
            serializersModule = SerializersModule {
                contextual(Date::class, DateSerializer)
            }
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType())
    }

    private fun createOkHttpClient(tokenDelegate: TokenDelegate?): OkHttpClient {
        return with(OkHttpClient().newBuilder()) {
            timeout?.let {
                readTimeout(it, TimeUnit.MILLISECONDS)
                writeTimeout(it, TimeUnit.MILLISECONDS)
                connectTimeout(it, TimeUnit.MILLISECONDS)
            }
            addInterceptor(UserAgentInterceptor(userAgent))
            tokenDelegate?.let {
                addInterceptor(AuthorizationInterceptor(tokenDelegate))
            }
            addInterceptor(HttpLoggingInterceptor().setLevel(httpLoggingInterceptorLevel))
            build()
        }
    }
}