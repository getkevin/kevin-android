package eu.kevin.core.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.Insets
import androidx.core.view.*

fun View.fadeOut() {
    if (visibility == GONE) return
    animate().apply {
        duration = 200L
        alpha(0f)
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                this@fadeOut.visibility = GONE
            }
        })
    }.start()
}

fun View.fadeIn() {
    if (visibility == VISIBLE) return
    animate().apply {
        duration = 200L
        alpha(1f)
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                this@fadeIn.visibility = VISIBLE
            }
        })
    }.start()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.applySystemInsetsPadding(top: Boolean = false, bottom: Boolean = false) {
    val originalPaddingTop = paddingTop
    val originalPaddingBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (top) {
            view.updatePadding(top = originalPaddingTop + insets.top)
        }
        if (bottom) {
            view.updatePadding(bottom = originalPaddingBottom + insets.bottom)
        }
        windowInsets
    }
}

fun View.applySystemInsetsMargin(top: Boolean = false, bottom: Boolean = false) {
    val originalMarginTop = marginTop
    val originalMarginBottom = marginBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        if (top) {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = originalMarginTop + insets.top
            }
        }
        if (bottom) {
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = originalMarginBottom + insets.bottom
            }
        }
        windowInsets
    }
}

fun View.listenForKeyboardInsets() {
    var view: View? = null
    var lastWindowInsets: WindowInsetsCompat? = null

    var deferredInsets = false

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        view = v
        lastWindowInsets = windowInsets
        if (!deferredInsets) {
            val typesInset = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
            val otherInset = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val diff = Insets.subtract(typesInset, otherInset).let {
                Insets.max(it, Insets.NONE)
            }
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = diff.bottom
            }
        }
        windowInsets
    }

    ViewCompat.setWindowInsetsAnimationCallback(this, object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {

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
            this@listenForKeyboardInsets.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = (diff.bottom)
            }
            return insets
        }

        override fun onEnd(animation: WindowInsetsAnimationCompat) {
            if (deferredInsets && animation.typeMask != 0) {

                deferredInsets = false

                if (lastWindowInsets != null && view != null) {
                    ViewCompat.dispatchApplyWindowInsets(view!!, lastWindowInsets!!)
                }
            }
        }
    })
}