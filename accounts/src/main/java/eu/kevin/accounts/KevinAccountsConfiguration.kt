package eu.kevin.accounts

import eu.kevin.core.plugin.KevinException

class KevinAccountsConfiguration private constructor(
    private val callbackUrl: String,
    private val showUnsupportedBanksInAccountLinking: Boolean
) {
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    fun getCallbackUrl(): String {
        return callbackUrl
    }

    fun getShowUnsupportedBanksInAccountLinking(): Boolean {
        return showUnsupportedBanksInAccountLinking
    }

    class Builder {

        private lateinit var callbackUrl: String
        private var showUnsupportedBanksInAccountLinking: Boolean = false

        /**
         * @param url callback url to be used in accounts plugin
         */
        fun setCallbackUrl(url: String): Builder {
            this.callbackUrl = url
            return this
        }

        /**
         * @param show account linking bank selection will list unsupported banks if true
         * used only for testing, shouldn't be changed in normal scenarios
         * default - false
         */
        fun setShowUnsupportedBanksInAccountLinking(show: Boolean): Builder  {
            this.showUnsupportedBanksInAccountLinking = show
            return this
        }

        fun build(): KevinAccountsConfiguration {
            if (!::callbackUrl.isInitialized) {
                throw KevinException("callback url is not provided!")
            }
            return KevinAccountsConfiguration(
                callbackUrl = callbackUrl,
                showUnsupportedBanksInAccountLinking = showUnsupportedBanksInAccountLinking
            )
        }
    }
}