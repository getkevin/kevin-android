package eu.kevin.core.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager

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