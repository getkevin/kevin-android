package eu.kevin.common.architecture

import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.context.KevinContextWrapper
import eu.kevin.common.extensions.getStyleFromAttr
import eu.kevin.common.extensions.setAnimationsFromStyle
import eu.kevin.core.R
import eu.kevin.core.entities.SessionResult
import eu.kevin.core.plugin.Kevin

abstract class BaseFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            handleBackPress()
        }
    }

    private fun handleBackPress() {
        with(supportFragmentManager) {
            fragments.lastOrNull { it.isVisible }?.let { fragment ->
                if (fragment !is Navigable || !fragment.onBackPressed()) {
                    handleBack()
                }
            } ?: handleBack()
        }
    }

    protected open fun handleBack() {
        with(supportFragmentManager) {
            if (backStackEntryCount == 1) {
                returnActivityResult(SessionResult.Canceled)
            } else {
                popBackStack()
            }
        }
    }

    protected abstract fun returnActivityResult(result: SessionResult<*>)

    protected fun pushFragment(fragmentContainerId: Int, fragment: Fragment) {
        with(supportFragmentManager) {
            commit {
                if (backStackEntryCount != 0) {
                    setAnimationsFromStyle(
                        getStyleFromAttr(R.attr.kevinWindowTransitionStyle),
                        this@BaseFragmentActivity
                    )
                }
                add(fragmentContainerId, fragment, fragment::class.simpleName)
                addToBackStack(fragment::class.simpleName)
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val locale = Kevin.getLocale()
        if (newBase != null && locale != null) {
            val context = KevinContextWrapper.wrap(newBase, locale)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }
}