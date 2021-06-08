package eu.kevin.core.networking.interceptors

import eu.kevin.core.networking.TokenDelegate
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

class AuthorizationInterceptor(private val delegate: TokenDelegate) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val originalToken = delegate.currentToken

        val request = originalToken?.let { token ->
            originalRequest.newBuilder().authorize(token).build()
        } ?: originalRequest

        val response = chain.proceed(request)

        if (originalToken == null || response.code != HTTP_UNAUTHORIZED) return response

        val latestToken = synchronized(this) {
            val latestToken = delegate.currentToken
            if (originalToken == latestToken) {
                delegate.getNewToken()
            } else {
                latestToken
            }
        }

        return latestToken?.let { token ->
            response.body?.close()
            chain.proceed(originalRequest.newBuilder().authorize(token).build())
        } ?: response
    }
}

private fun Request.Builder.authorize(token: String): Request.Builder {
    return header("Authorization", "Bearer $token")
}