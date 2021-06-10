package eu.kevin.core.architecture

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import eu.kevin.core.R
import eu.kevin.core.architecture.interfaces.Navigable
import eu.kevin.core.entities.ActivityResult

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
                returnActivityResult(ActivityResult.Canceled)
            } else {
                popBackStack()
            }
        }
    }

    protected abstract fun returnActivityResult(result: ActivityResult<*>)

    protected fun pushFragment(fragmentContainerId: Int, fragment: Fragment) {
        with(supportFragmentManager) {
            commit {
                setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                add(fragmentContainerId, fragment, fragment::class.simpleName)
                addToBackStack(fragment::class.simpleName)
            }
        }
    }
}