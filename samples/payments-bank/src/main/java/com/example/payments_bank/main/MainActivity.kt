package com.example.payments_bank.main

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import eu.kevin.core.enums.KevinCountry
import eu.kevin.inapppayments.paymentsession.PaymentSessionContract
import eu.kevin.inapppayments.paymentsession.entities.PaymentSessionConfiguration
import eu.kevin.inapppayments.paymentsession.enums.PaymentType
import eu.kevin.samples.payments_bank.R
import eu.kevin.samples.payments_bank.databinding.KevinActivityMainBinding
import kotlinx.coroutines.launch
import java.util.UUID

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    /**
     * ActivityResult callback used to obtain payment initiation session result.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/payment-initiation
     */
    private val paymentInitiationSession = registerForActivityResult(PaymentSessionContract()) { result ->
        viewModel.handlePaymentInitiationResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KevinActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                viewModel.uiState.collect {
                    updateUiState(it)
                }
            }
        }

        binding.initiatePaymentButton.setOnClickListener {
            viewModel.initiateBankPayment()
        }
    }

    private fun updateUiState(uiState: MainUiState) {
        with(binding) {
            initiatePaymentButton.visibility = if (uiState.isLoading) GONE else VISIBLE
            paymentText.visibility = if (uiState.isLoading) GONE else VISIBLE
            creditorText.visibility = if (uiState.isLoading) GONE else VISIBLE
            progressBar.visibility = if (uiState.isLoading) VISIBLE else GONE

            uiState.paymentId?.let { paymentId ->
                initiateBankPayment(
                    paymentId = paymentId,
                    paymentCountry = uiState.paymentCountry
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
     * Initiate unlinked bank account payment process.
     * Can be configured with various options like preselected country, bank and others.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/payment-initiation
     */
    private fun initiateBankPayment(
        paymentId: UUID,
        paymentCountry: KevinCountry?
    ) {
        // Payment session must be initiated with paymentId obtained via kevin. API
        val configuration = PaymentSessionConfiguration.Builder(paymentId.toString())
            .setPaymentType(PaymentType.BANK)
            .setCountryFilter(listOf(paymentCountry))
            .build()

        paymentInitiationSession.launch(configuration)
        viewModel.onPaymentInitiated()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_SHORT).show()
        viewModel.onUserMessageShown()
    }
}