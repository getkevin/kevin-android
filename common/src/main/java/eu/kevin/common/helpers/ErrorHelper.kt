package eu.kevin.common.helpers

import android.content.Context
import eu.kevin.core.R
import eu.kevin.core.networking.exceptions.ApiError

object ErrorHelper {

    fun getMessage(context: Context, error: Throwable): String {
        return when {
            error is ApiError -> getApiErrorMessage(context, error)
            error.localizedMessage != null -> error.localizedMessage!!
            else -> context.getString(R.string.kevin_error_unknown)
        }
    }

    private fun getApiErrorMessage(context: Context, error: ApiError): String {
        return when {
            error.isNoInternet() -> context.getString(R.string.kevin_error_no_internet)
            else -> context.getString(R.string.kevin_error_unknown)
        }
    }
}