package eu.kevin.demo.accountlinking

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import eu.kevin.demo.databinding.KevinFragmentAccountLinkingBinding

internal class AccountLinkingView(context: Context) : FrameLayout(context) {

    private val binding = KevinFragmentAccountLinkingBinding.inflate(LayoutInflater.from(context), this, false)

}