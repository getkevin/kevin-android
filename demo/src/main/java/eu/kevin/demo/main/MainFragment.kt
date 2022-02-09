package eu.kevin.demo.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import eu.kevin.common.extensions.setFragmentResultListener
import eu.kevin.core.entities.SessionResult
import eu.kevin.core.enums.KevinCountry
import eu.kevin.demo.auth.entities.ApiPayment
import eu.kevin.demo.countryselection.CountrySelectionContract
import eu.kevin.demo.main.entities.CreditorListItem
import eu.kevin.inapppayments.paymentsession.PaymentSessionContract
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainFragment : Fragment(), MainViewCallback {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(this)
    }

    private val makePayment = registerForActivityResult(PaymentSessionContract()) { result ->
        when (result) {
            is SessionResult.Success -> {
                Toast.makeText(
                    requireContext(),
                    "Payment ID: ${result.value.paymentId}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is SessionResult.Canceled -> {
                Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
            }
            is SessionResult.Failure -> {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var contentView: MainView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        observeChanges()
        listenForCountrySelectedResult()
        return MainView(inflater.context).also {
            it.callback = this
            contentView = it
        }
    }

    private fun observeChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.viewState.onEach { viewState ->
                contentView.update(viewState)
            }.launchIn(this)

            viewModel.viewAction.onEach { action ->
                when (action) {
                    is MainViewAction.OpenPaymentSession -> {
                        openPaymentSession(action.payment, action.paymentType)
                    }
                    is MainViewAction.ShowFieldValidations -> {
                        contentView.showInputFieldValidations(
                            action.emailValidationResult,
                            action.amountValidationResult,
                            action.termsAccepted
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    private fun listenForCountrySelectedResult() {
        parentFragmentManager.setFragmentResultListener(CountrySelectionContract, this) {
            viewModel.onCountrySelected(it)
        }
    }

    private fun openPaymentSession(payment: ApiPayment, paymentType: PaymentType) {
        val config = PaymentSessionConfiguration.Builder(payment.id)
            .setPaymentType(paymentType)
            .setPreselectedCountry(KevinCountry.LITHUANIA)
            .setSkipBankSelection(false)
            .build()
        makePayment.launch(config)
    }

    // MainViewCallback

    override fun onProceedClick(
        email: String,
        amount: String,
        termsAccepted: Boolean
    ) {
        viewModel.onProceedClick(
            email = email,
            amount = amount,
            isTermsAccepted = termsAccepted
        )
    }

    override fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onPaymentTypeSelected(position: Int) {
        viewModel.onPaymentTypeChanged(position)
    }

    override fun onCreditorSelected(creditor: CreditorListItem) {
        viewModel.onCreditorSelected(creditor)
    }

    override fun onSelectCountryClick() {
        viewModel.onSelectCountryClick()
    }

    override fun onAmountChanged(amount: String) {
        viewModel.onAmountChanged(amount)
    }
}