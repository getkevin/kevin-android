package eu.kevin.demo.main

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kevin.core.extensions.applySystemInsetsPadding
import eu.kevin.demo.R
import eu.kevin.demo.databinding.FragmentMainBinding

class MainView(context: Context) : ConstraintLayout(context) {

    var callback: MainViewCallback? = null

    private val binding = FragmentMainBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColor(TypedValue().let {
            context.theme.resolveAttribute(R.attr.primaryBackgroundColor, it, true)
            it.data
        })
        binding.actionBar.applySystemInsetsPadding(top = true)
        binding.root.applySystemInsetsPadding(bottom = true)
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
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressView.visibility = if (show) VISIBLE else GONE
    }
}