package eu.kevin.demo.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import eu.kevin.common.extensions.dp
import eu.kevin.common.extensions.getColorFromAttr
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.demo.R

class HorizontalSelectionBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private var textColor = ContextCompat.getColor(context, R.color.gray_01)
    private var selectedItemTextColor =
        context.getColorFromAttr(R.attr.primaryTextColor)

    private val padding = dp(16)
    private var slidingViewWidth = 0
    private var currentItemIndex = 0

    private var itemViews = ArrayList<TextView>()

    private val slidingOverlay: View = View(context).also {
        addView(it)
    }

    private var itemContainer: LinearLayout = LinearLayout(context).also {
        it.orientation = LinearLayout.HORIZONTAL
        addView(it)
    }

    fun setItems(items: List<String>) = post {
        itemContainer.removeAllViews()
        itemContainer.weightSum = items.size.toFloat()
        items.forEachIndexed { index, item ->
            val itemView = createItem(item, index)
            itemContainer.addView(itemView)
            itemViews.add(itemView)
        }
        configureSlidingView(divisionCoefficient = items.size)
        setCurrentItem(currentItemIndex)
    }

    fun getCurrentItemIndex() = currentItemIndex

    private fun setCurrentItem(position: Int) {
        currentItemIndex = position
        itemViews.forEachIndexed { index, textView ->
            if (index == position) {
                textView.setTextColor(selectedItemTextColor)
            } else {
                textView.setTextColor(textColor)
            }
        }
        animateSlidingViewPosition(position)
    }

    private fun createItem(value: String, index: Int) = TextView(context).also {
        it.layoutParams = LinearLayout.LayoutParams(0, MATCH_PARENT).apply {
            weight = 1f
        }
        TextViewCompat.setTextAppearance(it, R.style.TextAppearance_Kevin_SelectionBar)
        if (index == currentItemIndex) {
            it.setTextColor(selectedItemTextColor)
        } else {
            it.setTextColor(textColor)
        }
        it.text = value
        it.gravity = Gravity.CENTER
        it.setDebounceClickListener {
            setCurrentItem(index)
        }
    }

    private fun configureSlidingView(divisionCoefficient: Int) {
        slidingViewWidth = (width / divisionCoefficient) - padding * 2
        slidingOverlay.layoutParams = LayoutParams(slidingViewWidth, dp(2)).apply {
            gravity = Gravity.BOTTOM
            marginEnd = padding
            marginStart = padding
        }
        GradientDrawable().apply {
            cornerRadius = dp(2).toFloat()
            setColor(ContextCompat.getColor(context, R.color.blue))
            slidingOverlay.background = this
        }
    }

    private fun animateSlidingViewPosition(index: Int) {
        ObjectAnimator.ofFloat(
            slidingOverlay,
            "translationX",
            slidingViewWidth.toFloat() * index + padding * (index * 2)
        ).apply {
            duration = 350
            start()
        }
    }
}