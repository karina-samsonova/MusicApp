package com.example.design_system

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class GenreButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var genreCode: String = ""

    private val imageView: ImageView
    private val textView: TextView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.genre_button, this, true)
        imageView = root.findViewById(R.id.imageViewPlate)
        textView = root.findViewById(R.id.textViewGenre)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GenreButton)
        genreCode = typedArray.getString(R.styleable.GenreButton_genreCode).toString()
        typedArray.recycle()
        when (genreCode) {
            Genre.ROCK.label -> {
                textView.text = resources.getString(R.string.rock)
                imageView.setBackgroundResource(R.drawable.rock)
            }

            Genre.JAZZ.label -> {
                textView.text = resources.getString(R.string.jazz)
                imageView.setBackgroundResource(R.drawable.jazz)

            }

            Genre.POP.label -> {
                textView.text = resources.getString(R.string.pop)
                imageView.setBackgroundResource(R.drawable.pop)

            }

            Genre.ELECTRO.label -> {
                textView.text = resources.getString(R.string.electro)
                imageView.setBackgroundResource(R.drawable.electro)

            }
        }
    }

}