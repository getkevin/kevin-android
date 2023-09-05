package eu.kevin.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import eu.kevin.sample.databinding.KevinActivityMainBinding
import eu.kevin.sample.samples.accountlinking.AccountLinkingActivity
import eu.kevin.sample.samples.payment.bank.BankPaymentActivity
import eu.kevin.sample.samples.uicustomization.UiCustomizationActivity

internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KevinActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary_background)

        with(binding) {
            accountLinkingButton.setOnClickListener {
                openActivity(AccountLinkingActivity::class.java)
            }

            bankPaymentButton.setOnClickListener {
                openActivity(BankPaymentActivity::class.java)
            }

            uiCustomizationButton.setOnClickListener {
                openActivity(UiCustomizationActivity::class.java)
            }
        }
    }

    private fun openActivity(activityClass: Class<*>) {
        val intent = Intent(this@MainActivity, activityClass)
        startActivity(intent)
    }
}