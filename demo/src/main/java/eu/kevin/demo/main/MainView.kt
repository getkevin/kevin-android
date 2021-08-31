package eu.kevin.demo.main

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import eu.kevin.demo.R
import eu.kevin.demo.databinding.FragmentMainBinding
import eu.kevin.demo.extensions.applySystemInsetsPadding
import eu.kevin.demo.extensions.setDebounceClickListener

class MainView(context: Context) : ConstraintLayout(context) {

    var callback: MainViewCallback? = null

    private val binding = FragmentMainBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundColor(TypedValue().let {
            context.theme.resolveAttribute(R.attr.primaryBackgroundColor, it, true)
            it.data
        })
        binding.actionBar.apply {
            applySystemInsetsPadding(top = true)
            setNavigationContentDescription(R.string.navigate_back_content_description)
            setNavigationOnClickListener {
                callback?.onBackPressed()
            }
        }
        binding.root.applySystemInsetsPadding(bottom = true)
        initListeners()
    }

    fun update(state: MainViewState) {
        when (state) {
            is MainViewState.Loading -> showLoading(state.isLoading)
        }
    }

    private fun initListeners() {
        binding.linkAccountButton.setDebounceClickListener {
            callback?.onLinkAccountPressed()
        }
        binding.makeBankPaymentButton.setDebounceClickListener {
            callback?.onMakeBankPaymentPressed()
        }
        binding.makeCardPaymentButton.setDebounceClickListener {
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