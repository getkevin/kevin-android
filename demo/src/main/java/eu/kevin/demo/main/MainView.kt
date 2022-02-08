package eu.kevin.demo.main

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import eu.kevin.common.entities.LoadingState
import eu.kevin.common.entities.isLoading
import eu.kevin.common.extensions.fadeIn
import eu.kevin.common.extensions.fadeOut
import eu.kevin.common.helpers.ErrorHelper
import eu.kevin.common.helpers.SnackbarHelper
import eu.kevin.demo.R
import eu.kevin.demo.databinding.FragmentMainBinding
import eu.kevin.demo.extensions.setDebounceClickListener
import eu.kevin.demo.helpers.CountryHelper
import eu.kevin.demo.helpers.PaymentTypeHelper
import eu.kevin.demo.helpers.SpannableStringHelper
import eu.kevin.demo.helpers.SpannableStringLink
import eu.kevin.demo.main.adapter.CreditorsAdapter
import eu.kevin.demo.views.NumberTextWatcher
import eu.kevin.inapppayments.paymentsession.enums.PaymentType

class MainView(context: Context) : FrameLayout(context) {

    var callback: MainViewCallback? = null

    private val binding = FragmentMainBinding.inflate(LayoutInflater.from(context), this)
    private val creditorsAdapter = CreditorsAdapter {
        callback?.onCreditorSelected(it)
    }

    init {
        with(binding) {
            creditorsRecyclerView.adapter = creditorsAdapter

            termsTextView.text = SpannableStringHelper.getSpannableWithLinks(
                context.getString(R.string.window_main_terms_privacy_policy),
                ContextCompat.getColor(context, R.color.blue),
                SpannableStringLink(context.getString(R.string.window_main_terms_privacy_policy_clickable_terms)) {
                    callback?.openUrl(
                        context.getString(R.string.terms_url)
                    )
                },
                SpannableStringLink(context.getString(R.string.window_main_terms_privacy_policy_clickable_policy)) {
                    callback?.openUrl(
                        context.getString(R.string.privacy_policy_url)
                    )
                }
            )
            termsTextView.movementMethod = LinkMovementMethod()

            paymentTypeSelectionBar.setItems(
                PaymentType.values().map {
                    context.getString(PaymentTypeHelper.getStringRes(it))
                }
            )

            amountTextField.editText?.addTextChangedListener(
                NumberTextWatcher(
                    amountTextField.editText!!,
                    resources.configuration.locales[0],
                    2
                )
            )
        }
        initListeners()
    }

    fun update(state: MainViewState) {
        creditorsAdapter.update(state.creditors)
        when (state.loadingState) {
            is LoadingState.Loading -> startLoading(state.loadingState.isLoading())
            is LoadingState.FailureWithMessage -> showError(state.loadingState.message)
            is LoadingState.Failure -> {
                showError(ErrorHelper.getMessage(context, state.loadingState.error))
            }
        }
        with(binding) {
            proceedButton.text =
                context.getString(R.string.window_main_proceed_button, state.buttonText)

            countryFlagImageView.setImageDrawable(
                CountryHelper.getCountryFlagDrawable(
                    context,
                    state.selectedCountry
                )
            )
            selectedCountryTextView.text =
                CountryHelper.getCountryName(context, state.selectedCountry)
            creditorsRecyclerView.isInvisible = state.loadingCreditors
            creditorsProgressBar.isGone = !state.loadingCreditors
            amountTextField.error = state.amountError
            emailTextField.error = state.emailError
            termsErrorImageView.isGone = !state.termsError
            if (state.emailError == null) {
                emailTextField.isErrorEnabled = false
            }
            if (state.amountError == null) {
                amountTextField.isErrorEnabled = false
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            proceedButton.setOnClickListener {
                callback?.onProceedClick()
            }

            termsCheckbox.setOnCheckedChangeListener { _, checked ->
                callback?.onTermsCheckboxChanged(checked)
            }

            emailTextField.editText?.addTextChangedListener {
                callback?.onEmailChanged(it?.toString() ?: "")
            }

            amountTextField.editText?.addTextChangedListener {
                callback?.onAmountChanged(it?.toString()?.replace(",", "") ?: "")
            }

            countrySelectionContainer.setDebounceClickListener {
                callback?.onSelectCountryClick()
            }

            paymentTypeSelectionBar.setOnItemSelectedListener {
                callback?.onPaymentTypeSelected(it)
            }
        }
    }

    private fun startLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressView.fadeIn()
            } else {
                progressView.fadeOut()
            }
        }
    }

    private fun showError(message: String) {
        binding.progressView.fadeOut()
        SnackbarHelper.showError(this, message)
    }
}