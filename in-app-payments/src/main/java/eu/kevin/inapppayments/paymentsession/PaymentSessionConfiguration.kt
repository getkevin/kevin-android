package eu.kevin.inapppayments.paymentsession

import android.os.Parcelable
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.parcelize.Parcelize

/**
 * Configuration class for [eu.kevin.inapppayments.paymentsession.PaymentSession]
 */
@Parcelize
data class PaymentSessionConfiguration(
    val paymentId: String,
    val paymentType: PaymentType,
    val preselectedCountry: String?,
    val preselectedBank: String?,
    val skipBankSelection: Boolean
) : Parcelable {

    init {
        if (skipBankSelection) {
            requireNotNull(preselectedBank) {
                "if skipCountrySelection is true, preselectedBank must be provided"
            }
        }
    }

    /**
     * @property paymentId id of payment to be executed
     * @property paymentType [PaymentType] type of payment method to be used during payment session
     */
    class Builder(private val paymentId: String, private val paymentType: PaymentType) {
        private var preselectedCountry: String? = null
        private var preselectedBank: String? = null
        private var skipBankSelection: Boolean = false

        /**
         * @param country country iso code that will be used during initial
         * PaymentSession setup. If country is provided, it will be preselected
         * in bank selection window.
         *
         * Default 'null'
         */
        fun setPreselectedCountry(country: String): Builder {
            this.preselectedCountry = country
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
         * Bank has to be provided with [setPreselectedBank] for this to work,
         * otherwise calling [build] will throw [IllegalArgumentException]
         *
         * Default is 'false'
         */
        fun setSkipBankSelection(skip: Boolean): Builder {
            this.skipBankSelection = skip
            return this
        }

        fun build(): PaymentSessionConfiguration {
            return PaymentSessionConfiguration(
                paymentId,
                paymentType,
                preselectedCountry,
                preselectedBank,
                skipBankSelection
            )
        }
    }
}