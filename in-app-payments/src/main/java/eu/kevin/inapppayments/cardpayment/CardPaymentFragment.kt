package eu.kevin.inapppayments.cardpayment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.launchOnRepeat
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandleBackClicked
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandleCardPaymentWebEvent
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandleOnContinueClicked
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandlePageFinishedLoading
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandlePageStartLoading
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandlePaymentResult
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.HandleUserSoftRedirect
import eu.kevin.inapppayments.cardpayment.CardPaymentIntent.Initialize
import eu.kevin.inapppayments.cardpayment.CardPaymentViewAction.ShowFieldValidations
import eu.kevin.inapppayments.cardpayment.CardPaymentViewAction.SubmitCardForm
import eu.kevin.inapppayments.cardpayment.CardPaymentViewAction.SubmitUserRedirect
import eu.kevin.inapppayments.cardpayment.events.CardPaymentWebEvent
import eu.kevin.inapppayments.cardpaymentredirect.CardPaymentRedirectContract
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class CardPaymentFragment :
    BaseFragment<CardPaymentState, CardPaymentIntent, CardPaymentViewModel>(),
    CardPaymentViewDelegate {

    override val viewModel: CardPaymentViewModel by viewModels {
        CardPaymentViewModel.Factory(this)
    }

    var configuration: CardPaymentFragmentConfiguration? by savedState()

    private lateinit var view: CardPaymentView

    override fun onCreateView(context: Context): IView<CardPaymentState> {
        return CardPaymentView(context).also {
            it.delegate = this
            view = it
            observeViewActions()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchOnRepeat {
            viewModel.events.collect { this@CardPaymentFragment.view.handleEvent(it) }
        }
    }

    override fun onAttached() {
        super.onAttached()
        viewModel.intents.trySend(Initialize(configuration!!))
        parentFragmentManager.setFragmentResultListener(CardPaymentRedirectContract, this) { shouldRedirect ->
            viewModel.intents.trySend(HandleUserSoftRedirect(shouldRedirect))
        }
    }

    override fun onBackPressed(): Boolean {
        viewModel.intents.trySend(HandleBackClicked)
        return true
    }

    private fun observeViewActions() {
        viewModel.viewAction.onEach { action ->
            when (action) {
                is SubmitCardForm -> {
                    view.submitCardForm(
                        action.cardholderName,
                        action.cardNumber,
                        action.expiryDate,
                        action.cvv
                    )
                }
                is ShowFieldValidations -> {
                    view.showInputFieldValidations(
                        action.cardholderNameValidation,
                        action.cardNumberValidation,
                        action.expiryDateValidation,
                        action.cvvValidation
                    )
                }
                is SubmitUserRedirect -> {
                    view.submitUserRedirect(action.shouldRedirect)
                }
            }
        }.launchIn(lifecycleScope)
    }

    // CardPaymentViewDelegate

    override fun onBackClicked() {
        viewModel.intents.trySend(HandleBackClicked)
    }

    override fun onContinueClicked(
        cardholderName: String,
        cardNumber: String,
        expiryDate: String,
        cvv: String
    ) {
        viewModel.intents.trySend(HandleOnContinueClicked(cardholderName, cardNumber, expiryDate, cvv))
    }

    override fun onPageStartLoading() {
        viewModel.intents.trySend(HandlePageStartLoading)
    }

    override fun onPageFinishedLoading() {
        viewModel.intents.trySend(HandlePageFinishedLoading)
    }

    override fun onPaymentResult(uri: Uri) {
        viewModel.intents.trySend(HandlePaymentResult(uri))
    }

    override fun onWebEvent(event: CardPaymentWebEvent) {
        viewModel.intents.trySend(HandleCardPaymentWebEvent(event))
    }
}