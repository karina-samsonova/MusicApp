package com.example.design_system

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView

class ProgressPlate @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var imageView: ImageView = ImageView(context)

    val rotation = AnimationUtils.loadAnimation(context, R.anim.rotate)

    init {
        imageView.setImageResource(R.drawable.music_plate)
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        imageView.layout(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (visibility != VISIBLE) return
        imageView.draw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post {
            startAnimation(rotation)
        }
    }

    fun setVisible() {
        startAnimation(rotation)
        visibility = VISIBLE
    }

    fun setGone() {
        clearAnimation()
        visibility = GONE
    }
}