package eu.kevin.common.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.kevin.common.R
import eu.kevin.common.extensions.getDimensionFromAttr

class GridListItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val context = view.context
        val position = parent.getChildAdapterPosition(view)
        if (position % 2 == 0) {
            outRect.left = context.getDimensionFromAttr(R.attr.kevinMarginStart)
            outRect.right = context.getDimensionFromAttr(R.attr.kevinMarginEnd) / 2
        } else {
            outRect.left = context.getDimensionFromAttr(R.attr.kevinMarginStart) / 2
            outRect.right = context.getDimensionFromAttr(R.attr.kevinMarginEnd)
        }
    }
}