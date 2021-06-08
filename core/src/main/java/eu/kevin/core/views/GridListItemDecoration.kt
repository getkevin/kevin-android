package eu.kevin.core.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.kevin.core.extensions.dp

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
            outRect.left = context.dp(16)
            outRect.right = context.dp(8)
        } else {
            outRect.left = context.dp(8)
            outRect.right = context.dp(16)
        }
    }
}