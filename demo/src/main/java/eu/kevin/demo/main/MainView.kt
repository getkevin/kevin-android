package eu.kevin.demo.main

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kevin.demo.R
import eu.kevin.demo.databinding.FragmentMainBinding

class MainView(context: Context) : ConstraintLayout(context) {

    var callback: MainViewCallback? = null

    private val binding: FragmentMainBinding = FragmentMainBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColor(TypedValue().let {
            context.theme.resolveAttribute(R.attr.primaryBackgroundColor, it, true)
            it.data
        })
        initListeners()
    }

    fun update(state: MainViewState) {
        when (state) {
            is MainViewState.Loading -> showLoading(state.isLoading)
        }
    }

    private fun initListeners() {
        binding.linkAccountButton.setOnClickListener {
            callback?.onLinkAccountPressed()
        }
        binding.makeBankPaymentButton.setOnClickListener {
            callback?.onMakeBankPaymentPressed()
        }
        binding.makeCardPaymentButton.setOnClickListener {
            callback?.onMakeCardPaymentPressed()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressView.visibility = if (show) VISIBLE else GONE
    }
}