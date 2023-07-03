package eu.kevin.accounts.accountsession.entities

import android.os.Parcelable
import eu.kevin.accounts.accountsession.enums.AccountLinkingType
import eu.kevin.core.enums.KevinCountry
import kotlinx.parcelize.Parcelize

/**
 * Configuration class for [eu.kevin.accounts.accountsession.AccountSession]
 */
@Parcelize
data class AccountSessionConfiguration internal constructor(
    val state: String,
    val preselectedCountry: KevinCountry?,
    val disableCountrySelection: Boolean,
    val countryFilter: List<KevinCountry>,
    val bankFilter: List<String>,
    val preselectedBank: String?,
    val skipBankSelection: Boolean,
    val accountLinkingType: AccountLinkingType
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
        if (preselectedCountry != null && countryFilter.isNotEmpty()) {
            if (!countryFilter.contains(preselectedCountry)) {
                throw IllegalArgumentException("preselected country has to be included in countries filter")
            }
        }
    }

    /**
     * @property state state representing account linking session
     */
    class Builder(private val state: String) {
        private var preselectedCountry: KevinCountry? = null
        private var disableCountrySelection: Boolean = false
        private var countryFilter: List<KevinCountry> = emptyList()
        private var bankFilter: List<String> = emptyList()
        private var preselectedBank: String? = null
        private var skipBankSelection: Boolean = false
        private var accountLinkingType: AccountLinkingType = AccountLinkingType.BANK

        /**
         * @param linkingType [AccountLinkingType] that will be used to perform the linking
         *
         * Default [AccountLinkingType.BANK]
         */
        @Deprecated(
            "This method will be removed in the future versions of the SDK. " +
                "You can safely remove it from you configuration."
        )
        fun setLinkingType(linkingType: AccountLinkingType): Builder {
            accountLinkingType = linkingType
            return this
        }

        /**
         * @param country [KevinCountry] that will be used during initial
         * AccountLinkingSession setup. If country is provided, it will be preselected
         * in bank selection window.
         *
         * Default 'null'
         */
        fun setPreselectedCountry(country: KevinCountry?): Builder {
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
         * @param countries list of countries that will be shown during country selection. If this
         * list contains countries, only those that are in this list and are supported will be shown
         * in country selection. If list is empty, all supported countries will be shown.
         *
         * Default 'emptyList()'
         */
        fun setCountryFilter(countries: List<KevinCountry?>): Builder {
            this.countryFilter = countries.filterNotNull()
            return this
        }

        /**
         * @param banks list of bank ids that will be shown during bank selection. If this
         * list contains bank ids, only those that are in this list and are supported will be shown
         * in bank selection. If list is empty, all supported banks will be shown.
         *
         * Default 'emptyList()'
         */
        fun setBankFilter(banks: List<String>): Builder {
            this.bankFilter = banks.map { it.lowercase() }
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
         * Correct bank id has to be provided with [setPreselectedBank] for this to work.
         * If preselected country is provided, it has to match banks country, otherwise bank selection
         * won't be skipped.
         * If incorrect or unsupported bank id is provided, user will be asked to
         * select bank.
         *
         * Default is 'false'
         */
        fun setSkipBankSelection(skip: Boolean): Builder {
            this.skipBankSelection = skip
            return this
        }

        fun build(): AccountSessionConfiguration {
            return AccountSessionConfiguration(
                state,
                preselectedCountry,
                disableCountrySelection,
                countryFilter,
                bankFilter,
                preselectedBank,
                skipBankSelection,
                accountLinkingType
            )
        }
    }
}