package eu.kevin.accounts.linkingsession.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Configuration class for [eu.kevin.accounts.linkingsession.AccountLinkingSession]
 */
@Parcelize
data class AccountLinkingConfiguration internal constructor(
    val state: String,
    val preselectedCountry: String?,
    val disableCountrySelection: Boolean,
    val preselectedBank: String?,
    val skipBankSelection: Boolean
) : Parcelable {

    init {
        if (skipBankSelection) {
            requireNotNull(preselectedBank) {
                "if skipBankSelection is true, preselectedBank must be provided"
            }
        }
        if (disableCountrySelection) {
            requireNotNull(preselectedCountry) {
                "if disableCountrySelection is true, valid preselectedCountry must be provided"
            }
        }
    }

    /**
     * @property state state representing account linking session
     */
    class Builder(private val state: String) {
        private var preselectedCountry: String? = null
        private var disableCountrySelection: Boolean = false
        private var preselectedBank: String? = null
        private var skipBankSelection: Boolean = false

        /**
         * @param country country iso code that will be used during initial
         * AccountLinkingSession setup. If country is provided, it will be preselected
         * in bank selection window.
         *
         * Default 'null'
         */
        fun setPreselectedCountry(country: String): Builder {
            this.preselectedCountry = country
            return this
        }

        /**
         * @param isDisabled if true, country selection field will be disabled, and user will not be
         * able to select country. For this to work, valid country iso has to be provided with
         * [setPreselectedCountry]
         * If incorrect or unsupported country is provided, user will be able to select country.
         *
         * Default 'false'
         */
        fun setDisableCountrySelection(isDisabled: Boolean): Builder {
            this.disableCountrySelection = isDisabled
            return this
        }

        /**
         * @param bank bankId that will be used during initial AccountLinkingSession setup.
         * If bankId is provided, it will be preselected in bank selection window.
         * In order for bank to be preselected, correct country iso code of that bank has to be
         * provided with [setPreselectedCountry]
         *
         * Default 'null'
         */
        fun setPreselectedBank(bank: String): Builder {
            this.preselectedBank = bank
            return this
        }

        /**
         * @param skip if it's set to true, bank selection window will be skipped.
         * Bank has to be provided with [setPreselectedBank] for this to work,
         * otherwise calling [build] will throw [IllegalArgumentException]
         *
         * Default is 'false'
         */
        fun setSkipBankSelection(skip: Boolean): Builder {
            this.skipBankSelection = skip
            return this
        }

        fun build(): AccountLinkingConfiguration {
            return AccountLinkingConfiguration(
                state,
                preselectedCountry,
                disableCountrySelection,
                preselectedBank,
                skipBankSelection
            )
        }
    }
}