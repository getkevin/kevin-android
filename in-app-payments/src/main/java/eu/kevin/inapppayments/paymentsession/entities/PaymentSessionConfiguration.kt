package eu.kevin.inapppayments.paymentsession.entities

import android.os.Parcelable
import eu.kevin.core.enums.KevinCountry
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.parcelize.Parcelize

/**
 * Configuration class for [eu.kevin.inapppayments.paymentsession.PaymentSession]
 */
@Parcelize
data class PaymentSessionConfiguration(
    val paymentId: String,
    val paymentType: PaymentType,
    val preselectedCountry: KevinCountry?,
    val disableCountrySelection: Boolean,
    val countryFilter: List<KevinCountry>,
    val bankFilter: List<String>,
    val preselectedBank: String?,
    val skipBankSelection: Boolean,
    val skipAuthentication: Boolean
) : Parcelable {

    init {
        if (skipBankSelection) {
            requireNotNull(preselectedBank) {
                "if skipCountrySelection is true, preselectedBank must be provided"
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
     * @property paymentId id of payment to be executed
     * @property paymentType [PaymentType] type of payment method to be used during payment session
     */
    class Builder(private val paymentId: String) {
        private var paymentType: PaymentType = PaymentType.BANK
        private var preselectedCountry: KevinCountry? = null
        private var disableCountrySelection: Boolean = false
        private var countryFilter: List<KevinCountry> = emptyList()
        private var bankFilter: List<String> = emptyList()
        private var preselectedBank: String? = null
        private var skipBankSelection: Boolean = false
        private var skipAuthentication: Boolean = false

        /**
         * @param paymentType [PaymentType] that will be used to perform the payment
         *
         * Default [PaymentType.BANK]
         */
        fun setPaymentType(paymentType: PaymentType): Builder {
            this.paymentType = paymentType
            return this
        }

        /**
         * @param country [KevinCountry] that will be used during initial
         * PaymentSession setup. If country is provided, it will be preselected
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
         * @param bank bankId that will be used during initial PaymentSession setup.
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

        /**
         * @param skip if it's set to true, [paymentType] will be set to [PaymentType.BANK]
         * and country and bank selection windows will be skipped and user will be navigated
         * straight to payment confirmation.
         * The provided [paymentId] must be initialized with linkToken of linked bank account for this
         * to work properly.
         *
         * Default is 'false'
         */
        fun setSkipAuthentication(skip: Boolean): Builder {
            this.skipAuthentication = skip
            return this
        }

        fun build(): PaymentSessionConfiguration {
            return PaymentSessionConfiguration(
                paymentId,
                if (skipAuthentication) PaymentType.BANK else paymentType,
                preselectedCountry,
                disableCountrySelection,
                countryFilter,
                bankFilter,
                preselectedBank,
                skipBankSelection,
                skipAuthentication
            )
        }
    }
}