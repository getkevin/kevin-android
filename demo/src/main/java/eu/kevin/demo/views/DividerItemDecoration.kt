package eu.kevin.demo.views

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView

internal class DividerItemDecoration(private val dividerDrawable: Drawable) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        for (i in 0..parent.childCount - 2) {
            val child = parent.getChildAt(i)
            val dividerTop = child.bottom + (child.layoutParams as RecyclerView.LayoutParams).bottomMargin
            val dividerBottom = dividerTop + dividerDrawable.intrinsicHeight
            dividerDrawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            dividerDrawable.draw(canvas)
        }
    }
}