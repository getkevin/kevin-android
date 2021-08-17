package eu.kevin.demo.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

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