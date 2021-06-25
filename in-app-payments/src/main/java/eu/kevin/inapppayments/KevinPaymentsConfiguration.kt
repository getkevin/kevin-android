package eu.kevin.inapppayments

import eu.kevin.core.plugin.KevinException

class KevinPaymentsConfiguration private constructor(
    private val callbackUrl: String
) {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    fun getCallbackUrl(): String {
        return callbackUrl
    }

    class Builder {

        private lateinit var callbackUrl: String

        /**
         * @param url callback url to be used in in app payments plugin
         */
        fun setCallbackUrl(url: String): Builder {
            this.callbackUrl = url
            return this
        }

        fun build(): KevinPaymentsConfiguration {
            if (!::callbackUrl.isInitialized) {
                throw KevinException("callback url is not provided!")
            }
            return KevinPaymentsConfiguration(callbackUrl)
        }
    }
}