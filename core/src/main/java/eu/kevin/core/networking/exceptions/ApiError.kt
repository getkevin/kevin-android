package eu.kevin.core.networking.exceptions

import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class ApiError : Exception {
    var code: Int? = null
    var name: String? = null
    var description: String? = null
    var statusCode: Int? = null
    var data: Any? = null

    constructor(
        name: String? = null,
        description: String? = null,
        code: Int? = null,
        statusCode: Int? = null,
        data: Any? = null,
        cause: Throwable? = null
    ) : super(cause) {
        this.name = name
        this.description = description
        this.code = code
        this.statusCode = statusCode
        this.data = data
    }

    constructor(message: String, error: Throwable?) : super(message, error)

    constructor(message: String) : super(message)

    constructor(cause: Throwable?) : super(cause)

    fun isNoInternet(): Boolean {
        if (
            cause is UnknownHostException ||
            cause is SocketTimeoutException ||
            cause is SocketException ||
            cause is SSLException
        ) {
            return true
        }
        return name == "no_internet"
    }

    companion object {

        fun noInternet(): ApiError {
            return ApiError(name = "no_internet")
        }
        fun unknown(): ApiError {
            return ApiError(name = "unknown")
        }
    }
}