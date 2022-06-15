package eu.kevin.demo.main

import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.applyInsetter
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.demo.R
import eu.kevin.demo.databinding.KevinActivityMainBinding
import eu.kevin.demo.routing.DemoRouter
import kotlinx.coroutines.launch


internal class MainActivity : AppCompatActivity() {

    private lateinit var binding: KevinActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = KevinActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startListeningForRouteRequests()

        with(binding) {
            mainFragmentContainer.isUserInputEnabled = false
            mainFragmentContainer.adapter = TabAdapter(this@MainActivity)

            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.account_linking -> mainFragmentContainer.setCurrentItem(0, false)
                    R.id.payment -> mainFragmentContainer.setCurrentItem(1, false)
                }
                true
            }
            bottomNavigationView.selectedItemId = R.id.payment

            KeyboardManager(root).apply {
                onKeyboardVisibilityChanged {
                    if (it > 0) {
                        mainFragmentContainer.setPadding(0, 0, 0, 0)
                    }
                }
            }
            mainFragmentContainer.applyInsetter {
                type(ime = true, navigationBars = true) {
                    margin()
                }
            }
            bottomNavigationView.applyInsetter {
                type(navigationBars = true) {
                    padding()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.mainFragmentContainer.currentItem == 0) {
            finish()
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.account_linking
        }
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