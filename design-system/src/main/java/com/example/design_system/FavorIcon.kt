package com.example.design_system

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView

class FavorIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs), View.OnClickListener {

    private var isFavorite: Boolean = true

    private var outlinedHeartImageView: ImageView = ImageView(context)
    private var heartImageView: ImageView = ImageView(context)

    init {
        outlinedHeartImageView = ImageView(context).apply {
            setImageResource(R.drawable.ic_favorite)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(outlinedHeartImageView)

        heartImageView = ImageView(context).apply {
            setImageResource(R.drawable.ic_favorite_filled)
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        addView(heartImageView)

        setOnClickListener(this)
        loadAttr(attrs)
        updateHeartState(false)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FavorIcon)
        isFavorite = typedArray.getBoolean(R.styleable.FavorIcon_isFavorite, false)
        typedArray.recycle()
    }

    fun setFavorite(favorite: Boolean) {
        isFavorite = favorite
        updateHeartState(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        outlinedHeartImageView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )
        heartImageView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        outlinedHeartImageView.layout(0, 0, width, height)
        heartImageView.layout(0, 0, width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        outlinedHeartImageView.draw(canvas)
        heartImageView.draw(canvas)
    }

    private fun updateHeartState(animate: Boolean = true) {
        var currentScale = if (isFavorite) 0f else 1f
        val targetScale = if (isFavorite) 1f else 0f

        if (!animate) {
            heartImageView.scaleX = targetScale
            heartImageView.scaleY = targetScale
            return
        }

        val animator = ValueAnimator.ofFloat(currentScale, targetScale).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                currentScale = animation.animatedValue as Float
                heartImageView.scaleX = currentScale
                heartImageView.scaleY = currentScale
            }
        }
        animator.start()
    }

    fun doOnClick(favorite: Boolean) {
        isFavorite = favorite
        updateHeartState()
    }

    override fun onClick(v: View?) {
        doOnClick(!isFavorite)
    }
}