package eu.kevin.common.managers

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

class KeyboardManager(private val rootView: View) {

    private var onKeyboardSizeChanged: (Int) -> Unit = {}
    private var onKeyboardVisibilityChanged: (Int) -> Unit = {}

    private var lastWindowInsets: WindowInsetsCompat? = null
    private var deferredInsets = false
    private var lastKeyboardHeight = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (rootView.context as? Activity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        } else {
            (rootView.context as? Activity)?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        listenForKeyboardInsets()
        listenForKeyboardInsetsAnimationCallback()
    }

    fun onKeyboardSizeChanged(action: (Int) -> Unit) {
        onKeyboardSizeChanged = action
    }

    fun onKeyboardVisibilityChanged(action: (Int) -> Unit) {
        onKeyboardVisibilityChanged = action
    }

    private fun listenForKeyboardInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, windowInsets ->
            lastWindowInsets = windowInsets
            if (!deferredInsets) {
                val typesInset = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                val otherInset = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                val diff = Insets.subtract(typesInset, otherInset).let {
                    Insets.max(it, Insets.NONE)
                }
                onKeyboardSizeChanged.invoke(diff.bottom)
                lastKeyboardHeight = diff.bottom
            }
            windowInsets
        }
    }

    private fun listenForKeyboardInsetsAnimationCallback() {
        ViewCompat.setWindowInsetsAnimationCallback(rootView, object : WindowInsetsAnimationCompat.Callback(
            DISPATCH_MODE_CONTINUE_ON_SUBTREE
        ) {
            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                if (animation.typeMask != 0) {
                    deferredInsets = true
                }
            }

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val diff = Insets.subtract(typesInset, otherInset).let {
                    Insets.max(it, Insets.NONE)
                }
                onKeyboardSizeChanged.invoke(diff.bottom)
                return insets
            }

            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                if (deferredInsets && animation.typeMask != 0) {
                    deferredInsets = false
                    if (lastWindowInsets != null) {
                        ViewCompat.dispatchApplyWindowInsets(rootView, lastWindowInsets!!)
                    }
                }
                onKeyboardVisibilityChanged.invoke(lastKeyboardHeight)
            }
        })
    }
}