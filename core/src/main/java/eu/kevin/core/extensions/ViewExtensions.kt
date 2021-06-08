package eu.kevin.core.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.fadeOut() {
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