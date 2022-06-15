package eu.kevin.demo.screens.chooseaccount

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.fragment.FragmentResultContract

internal object ChooseAccountContract : FragmentResultContract<Long>() {
    override val requestKey = "choose_account_request_key"
    override val resultKey = "choose_account_result_key"

    fun getFragment(): BottomSheetDialogFragment {
        return ChooseAccountFragment()
    }

    override fun parseResult(data: Bundle): Long {
        return data.getLong(resultKey)
    }
}