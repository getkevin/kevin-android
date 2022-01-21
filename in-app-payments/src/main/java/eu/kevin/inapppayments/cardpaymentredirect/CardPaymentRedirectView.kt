package eu.kevin.inapppayments.cardpaymentredirect

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kevin.common.extensions.applySystemInsetsMargin
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.inapppayments.databinding.FragmentCardPaymentRedirectBinding

internal class CardPaymentRedirectView(context: Context) : ConstraintLayout(context) {
    private val binding = FragmentCardPaymentRedirectBinding.inflate(LayoutInflater.from(context), this)

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
}