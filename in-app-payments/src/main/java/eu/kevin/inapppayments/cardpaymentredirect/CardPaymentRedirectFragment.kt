package eu.kevin.inapppayments.cardpaymentredirect

import android.content.Context
import android.view.View
import eu.kevin.common.architecture.BaseStatelessModalFragment
import eu.kevin.common.architecture.routing.GlobalRouter

internal class CardPaymentRedirectFragment :
    BaseStatelessModalFragment(),
    CardPaymentRedirectViewDelegate {

    private lateinit var contentView: CardPaymentRedirectView

    var configuration: CardPaymentRedirectFragmentConfiguration? by savedState()

    override fun onCreateView(context: Context): View {
        isCancelable = false
        return CardPaymentRedirectView(context).also {
            it.delegate = this
            contentView = it
            it.setupMessage(configuration?.bankName)
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