package eu.kevin.inapppayments.cardpayment

import android.content.Context
import androidx.fragment.app.viewModels
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandleBackClicked
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.Initialize

internal class CardPaymentFragment : BaseFragment<CardPaymentState, CardPaymentIntent, CardPaymentViewModel>(),
    CardPaymentViewDelegate {

    override val viewModel: CardPaymentViewModel by viewModels {
        CardPaymentViewModel.Factory(this)
    }

    var configuration: CardPaymentFragmentConfiguration? by savedState()

    override fun onCreateView(context: Context): IView<CardPaymentState> {
        return CardPaymentView(context).also {
            it.delegate = this
        }
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.trySend(Initialize(configuration!!))
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.trySend(HandleBackClicked)
        return true
    }

    // CardPaymentViewDelegate

    override fun onBackClicked() {
        viewModel.intents.trySend(HandleBackClicked)
    }
}