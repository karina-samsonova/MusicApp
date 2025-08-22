package com.example.design_system

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat

class ShuffleIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), View.OnClickListener {

    private var isEnabled: Boolean = true

    private var imageView: ImageView = ImageView(context)

    private val activeColor: Int by lazy {
        ContextCompat.getColor(context, R.color.textPrimary)
    }

    private val inactiveColor: Int by lazy {
        ContextCompat.getColor(context, R.color.textSecondary)
    }

    init {
        imageView = ImageView(context).apply {
            setImageResource(R.drawable.shuffle)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(imageView)

        setOnClickListener(this)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShuffleIcon)
        isEnabled = typedArray.getBoolean(R.styleable.ShuffleIcon_isEnabled, false)
        typedArray.recycle()
    }

    fun setIsEnabled(enabled: Boolean) {
        isEnabled = enabled
        imageView.setColorFilter(
            if (isEnabled) activeColor
            else inactiveColor
        )
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        imageView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        imageView.layout(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        imageView.draw(canvas)
    }

    override fun onClick(v: View?) {
        setIsEnabled(!isEnabled)
    }
}