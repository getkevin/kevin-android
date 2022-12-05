package eu.kevin.demo.screens.payment

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import eu.kevin.common.architecture.BaseFragment
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.demo.screens.chooseaccount.ChooseAccountContract
import eu.kevin.demo.screens.countryselection.CountrySelectionContract
import eu.kevin.demo.screens.payment.PaymentIntent.OnAccountSelected
import eu.kevin.demo.screens.payment.PaymentIntent.OnAmountChanged
import eu.kevin.demo.screens.payment.PaymentIntent.OnCountrySelected
import eu.kevin.demo.screens.payment.PaymentIntent.OnCreditorSelected
import eu.kevin.demo.screens.payment.PaymentIntent.OnDonationRequest
import eu.kevin.demo.screens.payment.PaymentIntent.OnOpenCountrySelection
import eu.kevin.demo.screens.payment.PaymentIntent.OnPaymentResult
import eu.kevin.demo.screens.payment.PaymentIntent.OnPaymentTypeSelected
import eu.kevin.demo.screens.payment.entities.CreditorListItem
import eu.kevin.demo.screens.payment.entities.DonationRequest
import eu.kevin.demo.screens.paymenttype.PaymentTypeContract
import eu.kevin.inapppayments.paymentsession.PaymentSessionContract
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PaymentFragment :
    BaseFragment<PaymentViewState, PaymentIntent, PaymentViewModel>(),
    PaymentViewCallback {

    override val viewModel: PaymentViewModel by activityViewModels {
        PaymentViewModel.Factory(
            requireContext(),
            this
        )
    }

    private val makePayment = registerForActivityResult(PaymentSessionContract()) { result ->
        viewModel.intents.trySend(OnPaymentResult(result))
    }

    override fun onCreateView(context: Context): IView<PaymentViewState> {
        observeChanges()
        listenForCountrySelectedResult()
        listenForPaymentTypeSelectedResult()
        listenForAccountSelectedResult()
        return PaymentView(context).also {
            it.callback = this
            contentView = it
        }
    }

    private fun observeChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewAction.onEach { action ->
                when (action) {
                    is PaymentViewAction.OpenPaymentSession -> {
                        makePayment.launch(action.paymentSessionConfiguration)
                    }
                    is PaymentViewAction.ShowFieldValidations -> {
                        (contentView as PaymentView).showInputFieldValidations(
                            action.emailValidationResult,
                            action.amountValidationResult,
                            action.termsAccepted
                        )
                    }
                    is PaymentViewAction.ShowSuccessDialog -> {
                        (contentView as PaymentView).showSuccessDialog()
                    }
                    is PaymentViewAction.ResetFields -> {
                        (contentView as PaymentView).resetFields()
                    }
                }
            }.launchIn(this)
        }
    }

    private fun listenForCountrySelectedResult() {
        parentFragmentManager.setFragmentResultListener(CountrySelectionContract, this) {
            viewModel.intents.trySend(OnCountrySelected(it))
        }
    }

    private fun listenForPaymentTypeSelectedResult() {
        parentFragmentManager.setFragmentResultListener(PaymentTypeContract, this) {
            viewModel.intents.trySend(OnPaymentTypeSelected(it))
        }
    }

    private fun listenForAccountSelectedResult() {
        parentFragmentManager.setFragmentResultListener(ChooseAccountContract, this) {
            viewModel.intents.trySend(OnAccountSelected(it))
        }
    }

    // MainViewCallback

    override fun onDonateClick(donationRequest: DonationRequest) {
        viewModel.intents.trySend(OnDonationRequest(donationRequest))
    }

    override fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onCreditorSelected(creditor: CreditorListItem) {
        viewModel.intents.trySend(OnCreditorSelected(creditor))
    }

    override fun onSelectCountryClick() {
        viewModel.intents.trySend(OnOpenCountrySelection)
    }

    override fun onAmountChanged(amount: String) {
        viewModel.intents.trySend(OnAmountChanged(amount))
    }
}