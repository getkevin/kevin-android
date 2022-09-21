package eu.kevin.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

fun View.dp(value: Int): Int = context.dp(value)
fun View.pxToDp(px: Int): Float = context.pxToDp(px)

fun View.fadeOut(duration: Long = 200L, onFinished: () -> Unit = {}) {
    animate().cancel()
    if (visibility == GONE) return
    animate().apply {
        this.duration = duration
        alpha(0f)
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.visibility = GONE
                onFinished()
            }
        })
    }.start()
}

fun View.fadeIn(duration: Long = 200L) {
    animate().cancel()
    if (visibility == VISIBLE) return
    this.alpha = 0f
    animate().apply {
        this.duration = duration
        alpha(1f)
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                this@fadeIn.visibility = VISIBLE
            }
        })
    }.start()
}

fun View.hideKeyboard() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
    } else {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }
}

fun View.showKeyboard() {
    if (requestFocus()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController?.show(WindowInsetsCompat.Type.ime())
        } else {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
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

fun View.setDebounceClickListener(debounceTime: Long = 600L, action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}