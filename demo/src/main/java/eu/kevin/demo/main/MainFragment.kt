package eu.kevin.demo.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import eu.kevin.accounts.countryselection.enums.KevinCountry
import eu.kevin.accounts.linkingsession.LinkAccountContract
import eu.kevin.accounts.linkingsession.entities.AccountLinkingConfiguration
import eu.kevin.core.entities.ActivityResult
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.PaymentSessionContract
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.demo.auth.entities.ApiPayment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainFragment : Fragment(), MainViewCallback {

    private val viewModel: MainViewModel by viewModels()

    private val linkAccount = registerForActivityResult(LinkAccountContract()) { result ->
        when (result) {
            is ActivityResult.Success -> {
                Toast.makeText(requireContext(), "Account token: ${result.value.linkToken}", Toast.LENGTH_SHORT).show()
            }
            is ActivityResult.Canceled -> {
                Toast.makeText(requireContext(), "Account linking cancelled", Toast.LENGTH_SHORT).show()
            }
            is ActivityResult.Failure -> {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val makePayment = registerForActivityResult(PaymentSessionContract()) { result ->
        when (result) {
            is ActivityResult.Success -> {
                Toast.makeText(requireContext(), "Payment ID: ${result.value.paymentId}", Toast.LENGTH_SHORT).show()
            }
            is ActivityResult.Canceled -> {
                Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
            }
            is ActivityResult.Failure -> {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var contentView: MainView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        observeChanges()
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
                    is MainViewAction.OpenAccountLinkingSession -> {
                        openAccountLinkingSession(action.state)
                    }
                    is MainViewAction.OpenPaymentSession -> {
                        openPaymentSession(action.payment, action.paymentType)
                    }
                }
            }.launchIn(this)
        }
    }

    private fun openPaymentSession(payment: ApiPayment, paymentType: PaymentType) {
        val config = PaymentSessionConfiguration.Builder(payment.id, paymentType)
            .setPreselectedCountry(KevinCountry.LITHUANIA)
            .setSkipBankSelection(false)
            .build()
        makePayment.launch(config)
    }

    private fun openAccountLinkingSession(state: String) {
        val config = AccountLinkingConfiguration.Builder(state)
            .setPreselectedCountry(KevinCountry.LATVIA)
            .setSkipBankSelection(true)
            .setPreselectedBank("SWEDBANK_LT")
            .setDisableCountrySelection(false)
            .build()
        linkAccount.launch(config)
    }

    // MainViewCallback

    override fun onLinkAccountPressed() {
        viewModel.initializeAccountLinking()
    }

    override fun onMakeBankPaymentPressed() {
        viewModel.initializePayment(PaymentType.BANK)
    }

    override fun onMakeCardPaymentPressed() {
        viewModel.initializePayment(PaymentType.CARD)
    }
}