package eu.kevin.sample.samples.payment.card

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import eu.kevin.inapppayments.paymentsession.PaymentSessionContract
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.sample.R
import eu.kevin.sample.databinding.KevinActivityPaymentsCardBinding
import kotlinx.coroutines.launch
import java.util.UUID

internal class CardPaymentActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityPaymentsCardBinding
    private val viewModel: CardPaymentViewModel by viewModels()

    /**
     * ActivityResult callback used to obtain payment initiation session result.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/payment-initiation
     */
    private val paymentInitiationSession =
        registerForActivityResult(PaymentSessionContract()) { result ->
            viewModel.handlePaymentInitiationResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KevinActivityPaymentsCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                viewModel.uiState.collect {
                    updateUiState(it)
                }
            }
        }

        binding.initiatePaymentButton.setOnClickListener {
            viewModel.initiateCardPayment()
        }
    }

    private fun updateUiState(uiState: CardPaymentUiState) {
        with(binding) {
            initiatePaymentButton.visibility = if (uiState.isLoading) GONE else VISIBLE
            paymentText.visibility = if (uiState.isLoading) GONE else VISIBLE
            creditorText.visibility = if (uiState.isLoading) GONE else VISIBLE
            paymentWarningText.visibility = if (uiState.isLoading) GONE else VISIBLE
            progressBar.visibility = if (uiState.isLoading) VISIBLE else GONE

            uiState.paymentId?.let { paymentId ->
                initiateCardPayment(
                    paymentId = paymentId
                )
            }
            uiState.paymentCreditor?.let { creditor ->
                val account = creditor.accounts.first()
                creditorText.text = getString(R.string.text_creditor_info, creditor.name, account.currencyCode)
            }
            uiState.userMessage?.let { message -> showSnackbar(message) }
        }
    }

    /**
     * Initiate card payment process.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/payment-initiation
     */
    private fun initiateCardPayment(paymentId: UUID) {
        // Payment session must be initiated with paymentId obtained via kevin. API
        val configuration = PaymentSessionConfiguration.Builder(paymentId.toString())
            .setPaymentType(PaymentType.CARD)
            .build()

        paymentInitiationSession.launch(configuration)
        viewModel.onPaymentInitiated()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_SHORT).show()
        viewModel.onUserMessageShown()
    }
}