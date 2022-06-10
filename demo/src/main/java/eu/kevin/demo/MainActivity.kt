package eu.kevin.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.demo.accountlinking.AccountLinkingFragment
import eu.kevin.demo.databinding.KevinActivityMainBinding
import eu.kevin.demo.payment.PaymentFragment
import eu.kevin.demo.routing.DemoRouter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = KevinActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startListeningForRouteRequests()

        val accountLinkingFragment = AccountLinkingFragment()
        val paymentFragment = PaymentFragment()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.account_linking -> setCurrentFragment(accountLinkingFragment)
                R.id.payment -> setCurrentFragment(paymentFragment)
            }
            true
        }
        binding.bottomNavigationView.selectedItemId = R.id.account_linking
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else super.onBackPressed()
    }

    private fun startListeningForRouteRequests() {
        lifecycleScope.launch {
            DemoRouter.addOnPushModalFragmentListener(this) { modalFragment ->
                modalFragment.show(supportFragmentManager, modalFragment::class.simpleName)
            }

            DemoRouter.addOnReturnFragmentResultListener(this) { result ->
                supportFragmentManager.setFragmentResult(result.contract, result.data)
            }
        }
    }
}