package eu.kevin.demo.main

import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat.animate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.common.managers.KeyboardManager
import eu.kevin.demo.R
import eu.kevin.demo.databinding.KevinActivityMainBinding
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

            KeyboardManager(root).onKeyboardVisibilityChanged {
                if (it == 0) {
                    val behavior = (bottomNavigationView.layoutParams as CoordinatorLayout.LayoutParams).behavior as HideBottomViewOnScrollBehavior
                    behavior.slideUp(bottomNavigationView)
                }
            }
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