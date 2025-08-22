package com.example.design_system

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.random.Random

class AnimatedBubbles @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var areWhite: Boolean = false
    private var maxSpeed: Float = 0.3f

    init {
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnimatedBubbles)
        areWhite = typedArray.getBoolean(R.styleable.AnimatedBubbles_areWhite, areWhite)
        maxSpeed = typedArray.getFloat(R.styleable.AnimatedBubbles_maxSpeed, maxSpeed)
        typedArray.recycle()
    }

    private val bubbleCount = 20
    private val minBubbleSize = 10f
    private val maxBubbleSize = 40f
    private val minSpeed = 0.05f
    private val spawnAreaHeight = 500f

    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = if (areWhite) Color.argb(90, 255, 255, 255)
        else ContextCompat.getColor(context, R.color.bubbles)
    }

    private val bubbles = mutableListOf<Bubble>()
    private var lastUpdateTime = 0L

    private inner class Bubble {
        var x: Float = 0f
        var y: Float = 0f
        val size: Float = Random.nextFloat() * (maxBubbleSize - minBubbleSize) + minBubbleSize
        val speed: Float = Random.nextFloat() * (maxSpeed - minSpeed) + minSpeed

        init {
            resetPosition()
        }

        fun resetPosition() {
            x = Random.nextFloat() * width
            y = height + Random.nextFloat() * spawnAreaHeight
        }

        fun update(deltaTime: Long) {
            y -= speed * deltaTime

            if (y + size < 0) {
                resetPosition()
            }
        }
    }

    init {
        repeat(bubbleCount) {
            bubbles.add(Bubble())
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bubbles.clear()
        repeat(bubbleCount) {
            bubbles.add(Bubble())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentTime = System.currentTimeMillis()
        val deltaTime = if (lastUpdateTime == 0L) 16 else (currentTime - lastUpdateTime)
        lastUpdateTime = currentTime

        bubbles.forEach { it.update(deltaTime) }

        bubbles.forEach { bubble ->
            canvas.drawCircle(bubble.x, bubble.y, bubble.size, bubblePaint)
        }

        if (isAttachedToWindow) {
            invalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        invalidate()
    }
}