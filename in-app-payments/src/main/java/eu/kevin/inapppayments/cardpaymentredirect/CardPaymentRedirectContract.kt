package eu.kevin.inapppayments.cardpaymentredirect

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract

object CardPaymentRedirectContract: FragmentResultContract<Boolean>() {
    override val requestKey = "card_payment_redirect_request_key"
    override val resultKey = "card_payment_redirect_result_key"

    fun getFragment(configuration: CardPaymentRedirectFragmentConfiguration): BottomSheetDialogFragment {
        return CardPaymentRedirectFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): Boolean {
        return data.getBoolean(resultKey)
    }
}