package eu.kevin.demo.main

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
            bottomNavigationView.selectedItemId = R.id.account_linking

            ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, windowInsets ->
                val navBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(bottom = navBarInsets.bottom)
                windowInsets
            }

            KeyboardManager(mainFragmentContainer, excludeNavBarInsets = false)
                .onKeyboardSizeChanged { keyboardHeight ->
                    val bottomInset = keyboardHeight - binding.bottomNavigationView.height
                    mainFragmentContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = if (bottomInset < 0) 0 else bottomInset
                    }
                }
        }

        onBackPressedDispatcher.addCallback(this) {
            handleBackPressed()
        }
    }

    private fun handleBackPressed() {
        if (binding.mainFragmentContainer.currentItem == 0) {
            finish()
        } else {
            binding.bottomNavigationView.selectedItemId = R.id.account_linking
        }
    }

    private fun startListeningForRouteRequests() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    DemoRouter.mainModalRouterFlow.collect { modalFragment ->
                        modalFragment.show(supportFragmentManager, modalFragment::class.simpleName)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    DemoRouter.fragmentResultFlow.collect { result ->
                        supportFragmentManager.setFragmentResult(result.contract, result.data)
                    }
                }
            }
        }
    }
}