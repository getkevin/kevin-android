package eu.kevin.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import eu.kevin.common.architecture.routing.GlobalRouter
import eu.kevin.common.extensions.setFragmentResult
import eu.kevin.demo.main.MainFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        listenForModalFragment()

        if (supportFragmentManager.backStackEntryCount == 0) {
            supportFragmentManager.commit {
                replace(R.id.mainRouterContainer, MainFragment(), MainFragment::class.simpleName)
                addToBackStack(MainFragment::class.simpleName)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else super.onBackPressed()
    }

    private fun listenForModalFragment() {
        lifecycleScope.launch {
            GlobalRouter.addOnPushModalFragmentListener(this) { modalFragment ->
                modalFragment.show(supportFragmentManager, modalFragment::class.simpleName)
            }

            GlobalRouter.addOnReturnFragmentResultListener(this) { result ->
                supportFragmentManager.setFragmentResult(result.contract, result.data)
            }
        }
    }
}