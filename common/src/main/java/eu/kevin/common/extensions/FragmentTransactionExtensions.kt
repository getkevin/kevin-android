package eu.kevin.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentTransaction

@SuppressLint("ResourceType")
fun FragmentTransaction.setAnimationsFromStyle(@StyleRes style: Int, context: Context) {
    val attrs = intArrayOf(
        android.R.attr.activityOpenEnterAnimation,
        android.R.attr.activityOpenExitAnimation,
        android.R.attr.activityCloseEnterAnimation,
        android.R.attr.activityCloseExitAnimation
    )
    val styledAttributes = context.obtainStyledAttributes(style, attrs)

    with(styledAttributes) {
        setCustomAnimations(
            getResourceId(0, 0),
            getResourceId(1, 0),
            getResourceId(2, 0),
            getResourceId(3, 0)
        )
    }
    styledAttributes.recycle()
}