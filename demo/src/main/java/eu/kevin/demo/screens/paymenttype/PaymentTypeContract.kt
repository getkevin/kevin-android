package eu.kevin.demo.screens.paymenttype

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract
import eu.kevin.demo.screens.paymenttype.enums.DemoPaymentType

internal object PaymentTypeContract : FragmentResultContract<DemoPaymentType>() {
    override val requestKey = "payment_type_request_key"
    override val resultKey = "payment_type_result_key"

    fun getFragment(configuration: PaymentTypeFragmentConfiguration): BottomSheetDialogFragment {
        return PaymentTypeFragment().also {
            it.configuration = configuration
        }
    }

    override fun parseResult(data: Bundle): DemoPaymentType {
        return data.getSerializable(resultKey) as DemoPaymentType
    }
}