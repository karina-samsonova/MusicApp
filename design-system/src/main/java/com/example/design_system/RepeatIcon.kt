package com.example.design_system

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat

class RepeatIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), View.OnClickListener {

    private var imageView: ImageView = ImageView(context)

    private val activeColor: Int by lazy {
        ContextCompat.getColor(context, R.color.textPrimary)
    }

    private val inactiveColor: Int by lazy {
        ContextCompat.getColor(context, R.color.textSecondary)
    }

    init {
        imageView = ImageView(context).apply {
            setImageResource(R.drawable.repeat)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(imageView)

        setOnClickListener(this)
    }

    fun setRepeatOff() {
        imageView.setImageResource(R.drawable.repeat)
        imageView.setColorFilter(inactiveColor)
        invalidate()
    }

    fun setRepeatOne() {
        imageView.setImageResource(R.drawable.repeat_one)
        imageView.setColorFilter(activeColor)
        invalidate()
    }

    fun setRepeatAll() {
        imageView.setImageResource(R.drawable.repeat)
        imageView.setColorFilter(activeColor)
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
        invalidate()
    }
}