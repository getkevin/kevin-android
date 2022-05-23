package eu.kevin.common.extensions

import java.net.URI

fun String.removeWhiteSpaces(): String {
    return this.replace(" ", "")
}

fun String.appendQuery(query: String): String {
    val oldUri = URI(this)

    var newQuery: String? = oldUri.query
    if (newQuery == null) {
        newQuery = query
    } else {
        newQuery += "&$query"
    }

    return URI(
        oldUri.scheme,
        oldUri.authority,
        oldUri.path,
        newQuery,
        oldUri.fragment
    ).toString()
}