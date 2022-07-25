package eu.kevin.demo.screens.paymenttype

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import eu.kevin.common.architecture.BaseView
import eu.kevin.common.architecture.interfaces.IView
import eu.kevin.common.extensions.applySystemInsetsPadding
import eu.kevin.demo.databinding.KevinFragmentPaymentTypeBinding

internal class PaymentTypeView(context: Context) :
    BaseView<KevinFragmentPaymentTypeBinding>(context),
    IView<PaymentTypeState, Nothing> {

    var callback: PaymentTypeViewCallback? = null

    override val binding = KevinFragmentPaymentTypeBinding.inflate(LayoutInflater.from(context), this)

    override fun render(state: PaymentTypeState) {
        binding.linkedPaymentContainer.isVisible = state.showLinkedAccountOption
    }

    init {
        with(binding) {
            root.applySystemInsetsPadding(bottom = true)
            bankPaymentContainer.setOnClickListener {
                callback?.onBankPaymentSelected()
            }
            linkedPaymentContainer.setOnClickListener {
                callback?.onLinkedPaymentSelected()
            }
            cardPaymentContainer.setOnClickListener {
                callback?.onCardPaymentSelected()
            }
        }
    }
}