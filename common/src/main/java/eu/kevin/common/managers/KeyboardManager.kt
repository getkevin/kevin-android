package eu.kevin.common.managers

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat

@Suppress("DEPRECATION")
class KeyboardManager(private val rootView: View, val excludeNavBarInsets: Boolean = true) {

    private var onKeyboardSizeChanged: (Int) -> Unit = {}
    private var onKeyboardVisibilityChanged: (Int) -> Unit = {}

    private var lastWindowInsets: WindowInsetsCompat? = null
    private var deferredInsets = false
    private var lastKeyboardHeight = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (rootView.context as? Activity)?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
            )
        } else {
            (rootView.context as? Activity)?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            )
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
                val imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                val diff = if (excludeNavBarInsets) {
                    val typesInset = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                    val systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    Insets.subtract(typesInset, systemBarInsets).let {
                        Insets.max(it, Insets.NONE)
                    }
                } else {
                    imeInsets
                }
                onKeyboardSizeChanged.invoke(diff.bottom)
                lastKeyboardHeight = diff.bottom
            }
            windowInsets
        }
    }

    private fun listenForKeyboardInsetsAnimationCallback() {
        ViewCompat.setWindowInsetsAnimationCallback(
            rootView,
            object : WindowInsetsAnimationCompat.Callback(
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
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val diff = if (excludeNavBarInsets) {
                        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        Insets.subtract(imeInsets, systemBarInsets).let {
                            Insets.max(it, Insets.NONE)
                        }
                    } else {
                        imeInsets
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
            }
        )
    }
}