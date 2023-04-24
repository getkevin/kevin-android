package eu.kevin.sample.samples.accountlinking

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import eu.kevin.accounts.accountsession.AccountSessionContract
import eu.kevin.accounts.accountsession.entities.AccountSessionConfiguration
import eu.kevin.core.enums.KevinCountry
import eu.kevin.sample.databinding.KevinActivityAccountLinkBinding
import kotlinx.coroutines.launch

internal class AccountLinkingActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityAccountLinkBinding
    private val viewModel: AccountLinkingViewModel by viewModels()

    /**
     * ActivityResult callback used to obtain account linking session result.
     *
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/account-linking#initialise-linking-callback
     */
    private val accountLinkingResult =
        registerForActivityResult(AccountSessionContract()) { result ->
            viewModel.handleAccountLinkingResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KevinActivityAccountLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED) {
                viewModel.uiState.collect {
                    updateUiState(it)
                }
            }
        }

        binding.linkAccountButton.setOnClickListener {
            viewModel.initiateAccountLinking()
        }
    }

    private fun updateUiState(uiState: AccountLinkingUiState) {
        with(binding) {
            linkAccountButton.visibility = if (uiState.isLoading) GONE else VISIBLE
            linkAccountText.visibility = if (uiState.isLoading) GONE else VISIBLE
            linkAccountProgress.visibility = if (uiState.isLoading) VISIBLE else GONE

            uiState.accountLinkingState?.let { state -> launchAccountLinking(state) }
            uiState.userMessage?.let { message -> showSnackbar(message) }
        }
    }

    /**
     * Start account linking session and configure to your requirements.
     * More info: https://developer.kevin.eu/home/mobile-sdk/android/account-linking
     */
    private fun launchAccountLinking(state: String) {
        // Account linking session must be configured with state obtained via kevin. API
        val configuration = AccountSessionConfiguration.Builder(state)
            .setPreselectedCountry(KevinCountry.LITHUANIA)
            .build()

        accountLinkingResult.launch(configuration)
        viewModel.onAccountLinkingInitiated()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.rootLayout, message, Snackbar.LENGTH_SHORT).show()
        viewModel.onUserMessageShown()
    }
}