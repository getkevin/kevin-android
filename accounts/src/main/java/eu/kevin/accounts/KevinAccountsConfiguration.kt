package eu.kevin.accounts

import eu.kevin.core.plugin.KevinException

class KevinAccountsConfiguration private constructor(
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
         * @param url callback url to be used in accounts plugin
         */
        fun setCallbackUrl(url: String): Builder {
            this.callbackUrl = url
            return this
        }

        fun build(): KevinAccountsConfiguration {
            if (!::callbackUrl.isInitialized) {
                throw KevinException("callback url is not provided!")
            }
            return KevinAccountsConfiguration(callbackUrl)
        }
    }
}