package com.example.design_system

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MoodButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var moodCode: String = ""

    private val container: ConstraintLayout
    private val textView: TextView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.mood_button, this, true)
        container = findViewById(R.id.container)
        textView = root.findViewById(R.id.textViewMood)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MoodButton)
        moodCode = typedArray.getString(R.styleable.MoodButton_moodCode).toString()
        typedArray.recycle()
        when (moodCode) {
            Mood.FUN.label -> {
                textView.text = resources.getString(R.string.`fun`)
                container.setBackgroundResource(R.drawable.`fun`)
            }

            Mood.SAD.label -> {
                textView.text = resources.getString(R.string.sad)
                container.setBackgroundResource(R.drawable.sad)

            }

            Mood.LOVE.label -> {
                textView.text = resources.getString(R.string.love)
                container.setBackgroundResource(R.drawable.love)

            }

            Mood.CALM.label -> {
                textView.text = resources.getString(R.string.calm)
                container.setBackgroundResource(R.drawable.calm)

            }
        }
    }

}