package eu.kevin.inapppayments.cardpaymentredirect

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.inapppayments.R
import eu.kevin.inapppayments.databinding.KevinFragmentCardPaymentRedirectBinding

internal class CardPaymentRedirectView(context: Context) : ConstraintLayout(context) {
    private val binding = KevinFragmentCardPaymentRedirectBinding.inflate(LayoutInflater.from(context), this)

    var delegate: CardPaymentRedirectViewDelegate? = null

    init {
        with(binding.noButton) {
            applySystemInsetsMargin(bottom = true)
            setDebounceClickListener {
                delegate?.onUserDeclined()
            }
        }
        with(binding.yesButton) {
            applySystemInsetsMargin(bottom = true)
            setDebounceClickListener {
                delegate?.onUserConfirmed()
            }
        }
    }

    fun setupMessage(bankName: String?) {
        binding.subtitleView.text = if (bankName != null) {
            context.getString(R.string.kevin_window_card_payment_bank_redirect_subtitle).format(bankName)
        } else {
            context.getString(R.string.kevin_window_card_payment_redirect_subtitle)
        }
    }
}