package com.example.design_system

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class PlayIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), View.OnClickListener {

    private var isPlaying: Boolean = true

    private var imageView: ImageView = ImageView(context)

    init {
        imageView = ImageView(context).apply {
            setImageResource(R.drawable.pause)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(imageView)

        setOnClickListener(this)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayIcon)
        isPlaying = typedArray.getBoolean(R.styleable.PlayIcon_isPlaying, false)
        typedArray.recycle()
    }

    fun setPlaying(playing: Boolean) {
        isPlaying = playing
        imageView.setImageResource(
            if (isPlaying) R.drawable.pause
            else R.drawable.play
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
        setPlaying(!isPlaying)
    }
}