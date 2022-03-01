package eu.kevin.common.extensions

import java.net.URI

fun String.removeWhiteSpaces(): String {
    return this.replace(" ", "")
}

fun String.appendQueryParameter(key: String, value: String): String {
    val oldUri = URI(this)

    var newQuery: String? = oldUri.query
    if (newQuery == null) {
        newQuery = "$key=$value"
    } else {
        newQuery += "&$key=$value"
    }

    return URI(
        oldUri.scheme, oldUri.authority,
        oldUri.path, newQuery, oldUri.fragment
    ).toString()
}