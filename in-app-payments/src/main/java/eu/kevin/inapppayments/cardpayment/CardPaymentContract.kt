package eu.kevin.inapppayments.cardpayment

import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.kevin.common.extensions.requireParcelable
import eu.kevin.common.fragment.FragmentResult
import eu.kevin.common.fragment.FragmentResultContract

object CardPaymentContract : FragmentResultContract<FragmentResult<CardPaymentResult>>() {
    override val requestKey = "card_payment_request_key"
    override val resultKey = "card_payment_result_key"

    fun getFragment(configuration: CardPaymentFragmentConfiguration): Fragment {
        return CardPaymentFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): FragmentResult<CardPaymentResult> {
        return data.requireParcelable(resultKey)
    }
}