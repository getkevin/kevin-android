package eu.kevin.inapppayments.paymentconfirmation

import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.common.fragment.FragmentResultContract

object PaymentConfirmationContract : FragmentResultContract<FragmentResult<PaymentConfirmationResult>>() {
    override val requestKey = "payment_confirmation_request_key"
    override val resultKey = "payment_confirmation_result_key"

    fun getFragment(configuration: PaymentConfirmationFragmentConfiguration): Fragment {
        return PaymentConfirmationFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): FragmentResult<PaymentConfirmationResult> {
        return data.getParcelable(resultKey)!!
    }
}