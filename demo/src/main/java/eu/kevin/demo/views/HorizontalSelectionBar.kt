package eu.kevin.demo.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import eu.kevin.common.extensions.dp
import eu.kevin.common.extensions.setDebounceClickListener
import eu.kevin.demo.R

class HorizontalSelectionBar : FrameLayout {

    private var textColor = ContextCompat.getColor(context, R.color.gray_01)
    var selectedItemTextColor = MaterialColors.getColor(context, R.attr.primaryTextColor, Color.BLACK)

    private var slidingViewWidth = 0

    private var currentItemIndex = 0

    private val slidingOverlay: View
    private var itemViews = ArrayList<TextView>()
    private lateinit var itemContainer: LinearLayout

    private var onItemSelectedCallback: (Int) -> Unit = {}

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        post {
            itemContainer = LinearLayout(context).also {
                it.orientation = LinearLayout.HORIZONTAL
                addView(it)
            }
        }
        slidingOverlay = View(context).also {
            addView(it)
        }
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

    private fun setCurrentItem(position: Int) {
        currentItemIndex = position
        itemViews.forEachIndexed { index, textView ->
            if(index == position) {
                textView.setTextColor(selectedItemTextColor)
            } else {
                textView.setTextColor(textColor)
            }
        }
        onItemSelectedCallback(position)
        animateSlidingViewPosition(position)
    }

    fun setOnItemSelectedListener(onItemSelected: (Int) -> Unit) {
        onItemSelectedCallback = onItemSelected
    }

    private fun createItem(value: String, index: Int) = TextView(context).also {
        it.layoutParams = LinearLayout.LayoutParams(0, MATCH_PARENT).apply {
            weight = 1f
        }
        it.setTypeface(null, Typeface.BOLD);
        if(index == currentItemIndex) {
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
        slidingViewWidth = (width / divisionCoefficient) - dp(16) * 2
        slidingOverlay.layoutParams = LayoutParams(slidingViewWidth, dp(2)).apply {
            gravity = Gravity.BOTTOM
            marginEnd = dp(16)
            marginStart = dp(16)
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
            slidingViewWidth.toFloat() * index + context.dp(16) * (index * 2)
        ).apply {
            duration = 350
            start()
        }
    }
}