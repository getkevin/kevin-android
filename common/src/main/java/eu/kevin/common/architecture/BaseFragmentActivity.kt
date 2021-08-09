package eu.kevin.common.architecture

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import eu.kevin.core.R
import eu.kevin.common.architecture.interfaces.Navigable
import eu.kevin.common.context.KevinContextWrapper
import eu.kevin.core.entities.SessionResult
import eu.kevin.common.extensions.getStyleFromAttr
import eu.kevin.common.extensions.setAnimationsFromStyle
import eu.kevin.core.plugin.Kevin

abstract class BaseFragmentActivity : AppCompatActivity() {

    override fun onBackPressed() {
        with(supportFragmentManager) {
            fragments.firstOrNull { it.isVisible }?.let { fragment ->
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