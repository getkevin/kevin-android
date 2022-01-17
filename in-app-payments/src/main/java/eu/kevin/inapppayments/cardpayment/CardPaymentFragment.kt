package eu.kevin.inapppayments.cardpayment

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.Initialize

internal class CardPaymentFragment : BaseFragment<CardPaymentState, CardPaymentIntent, CardPaymentViewModel>() {

    override val viewModel: CardPaymentViewModel by viewModels {
        CardPaymentViewModel.Factory(this)
    }

    var configuration: CardPaymentFragmentConfiguration? by savedState()

    override fun onCreateView(context: Context): IView<CardPaymentState> {
        return CardPaymentView(context)
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.trySend(Initialize(configuration!!))
    }
}