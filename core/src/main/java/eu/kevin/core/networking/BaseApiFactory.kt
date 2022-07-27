package eu.kevin.core.networking

import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.core.networking.exceptions.ErrorResponse
import eu.kevin.core.networking.serializers.DateSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.Date

abstract class BaseApiFactory<T : BaseApiClient>(
    private val baseUrl: String,
    private val userAgent: String,
    private val timeout: Int? = null,
    private val logLevel: LogLevel = LogLevel.NONE
) {

    private val jsonContentNegotiation = Json {
        serializersModule = SerializersModule {
            contextual(Date::class, DateSerializer)
        }
        ignoreUnknownKeys = true
    }

    abstract fun createClient(): T

    protected fun createKtorClient(): HttpClient {
        return HttpClient(Android) {
            engine {
                timeout?.let {
                    connectTimeout = it
                    socketTimeout = it
                }
            }

            defaultRequest {
                url.takeFrom(
                    URLBuilder().takeFrom(baseUrl).apply {
                        encodedPath += url.encodedPath
                    }
                )
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.UserAgent, userAgent)
            }

            HttpResponseValidator {
                handleResponseExceptionWithRequest { cause, _ ->
                    handleRequestError(cause)
                }
            }

            timeout?.let {
                install(HttpTimeout) {
                    requestTimeoutMillis = it.toLong()
                    socketTimeoutMillis = it.toLong()
                    connectTimeoutMillis = it.toLong()
                }
            }

            install(ContentNegotiation) {
                json(jsonContentNegotiation)
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = logLevel
            }
        }
    }

    private suspend fun handleRequestError(requestError: Throwable) {
        val errorToThrow = when (requestError) {
            is ClientRequestException -> {
                val errorString = requestError.response.bodyAsText()
                try {
                    val error = jsonContentNegotiation.decodeFromString(ErrorResponse.serializer(), errorString).error
                    ApiError(
                        error.name,
                        error.description,
                        error.code,
                        requestError.response.status.value,
                        requestError
                    )
                } catch (e: Exception) {
                    ApiError(
                        description = requestError.message,
                        statusCode = requestError.response.status.value,
                        cause = e
                    )
                }
            }
            else -> {
                ApiError(description = requestError.message, cause = requestError)
            }
        }
        throw errorToThrow
    }
}