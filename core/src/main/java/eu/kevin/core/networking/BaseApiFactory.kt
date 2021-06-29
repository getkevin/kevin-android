package eu.kevin.core.networking

import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.core.networking.exceptions.ErrorResponse
import eu.kevin.core.networking.serializers.DateSerializer
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.*

abstract class BaseApiFactory<T : BaseApiClient>(
    private val baseUrl: String,
    private val userAgent: String,
    private val timeout: Int? = null,
    private val logLevel: LogLevel = LogLevel.NONE
) {
    abstract fun createClient(tokenDelegate: TokenDelegate?): T

    protected fun createKtorClient(): HttpClient {
        return HttpClient(Android) {
            engine {
                timeout?.let {
                    connectTimeout = it
                    socketTimeout = it
                }
            }

            defaultRequest {
                url.takeFrom(URLBuilder().takeFrom(baseUrl).apply {
                    encodedPath += url.encodedPath
                })
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.UserAgent, userAgent)
            }

            HttpResponseValidator {
                handleResponseException {
                    handleRequestError(it)
                }
            }

            timeout?.let {
                install(HttpTimeout) {
                    requestTimeoutMillis = it.toLong()
                    socketTimeoutMillis = it.toLong()
                    connectTimeoutMillis = it.toLong()
                }
            }

            install(JsonFeature) {
                serializer = createSerializer()
            }

            install(Logging) {
                logger = Logger.ANDROID
                level = logLevel
            }
        }
    }

    protected open fun createSerializer(): KotlinxSerializer {
        return KotlinxSerializer(
            Json {
                serializersModule = SerializersModule {
                    contextual(Date::class, DateSerializer)
                }
                ignoreUnknownKeys = true
            }
        )
    }

    private suspend fun handleRequestError(requestError: Throwable) {
        val errorToThrow = when (requestError) {
            is ClientRequestException -> {
                val errorString = requestError.response.readText()
                try {
                    val error = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString(ErrorResponse.serializer(), errorString).error
                    ApiError(error.name, error.description, error.code, requestError.response.status.value, requestError)
                } catch (e: Exception) {
                    ApiError(description = requestError.message, statusCode = requestError.response.status.value, cause = e)
                }
            }
            else -> {
                ApiError(description = requestError.message, cause = requestError)
            }
        }
        throw errorToThrow
    }
}