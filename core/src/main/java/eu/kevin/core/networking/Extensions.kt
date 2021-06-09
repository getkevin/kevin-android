package eu.kevin.core.networking

import eu.kevin.core.networking.exceptions.ApiError
import eu.kevin.core.networking.exceptions.ErrorResponse
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.lang.Exception

inline fun <T> executeRequest(body: () -> T): T {
    try {
        return body.invoke()
    } catch (requestError: Exception) {
        val errorToThrow = when (requestError) {
            is HttpException -> {
                val errorString = requestError.response()?.errorBody()?.string() ?: throw ApiError.unknown()
                try {
                    val error = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString(ErrorResponse.serializer(), errorString).error
                    ApiError(error.name, error.description, error.code, requestError.code(), requestError)
                } catch (e: Exception) {
                    ApiError(description = requestError.message(), statusCode = requestError.code(), cause = e)
                }
            }
            else -> {
                ApiError(requestError.localizedMessage ?: "", requestError)
            }
        }
        throw errorToThrow
    }
}