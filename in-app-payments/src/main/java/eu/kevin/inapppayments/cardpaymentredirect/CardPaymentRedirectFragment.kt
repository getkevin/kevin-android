package eu.kevin.inapppayments.cardpaymentredirect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.kevin.common.architecture.routing.GlobalRouter

internal class CardPaymentRedirectFragment : BottomSheetDialogFragment(),
    CardPaymentRedirectViewDelegate {

    private lateinit var contentView: CardPaymentRedirectView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        return CardPaymentRedirectView(inflater.context).also {
            it.delegate = this
            contentView = it
        }
    }

    // CardPaymentRedirectViewDelegate

    override fun onUserConfirmed() {
        GlobalRouter.returnFragmentResult(CardPaymentRedirectContract, true)
        dismiss()
    }

    override fun onUserDeclined() {
        GlobalRouter.returnFragmentResult(CardPaymentRedirectContract, false)
        dismiss()
    }
}