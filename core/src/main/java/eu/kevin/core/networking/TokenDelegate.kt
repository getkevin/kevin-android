package eu.kevin.core.networking

interface TokenDelegate {
    val currentToken: String?
    fun getNewToken(): String?
}