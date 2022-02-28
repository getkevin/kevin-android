package eu.kevin.inapppayments.paymentconfirmation.helpers

import java.net.URI

fun String.appendQueryParameter(queryKey: String, queryValue: String): String {
    val oldUri = URI(this)

    var newQuery: String? = oldUri.query
    if (newQuery == null) {
        newQuery = "$queryKey=$queryValue"
    } else {
        newQuery += "&$queryKey=$queryValue"
    }

    return URI(
        oldUri.scheme, oldUri.authority,
        oldUri.path, newQuery, oldUri.fragment
    ).toString()
}