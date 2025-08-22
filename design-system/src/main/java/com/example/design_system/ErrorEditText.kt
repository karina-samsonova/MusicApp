package com.example.design_system

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView

class ErrorEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : RelativeLayout(context, attrs) {

    private val editText: EditText
    private val errorText: TextView
    private val btnToggle: ImageView

    init {
        val root = LayoutInflater.from(context).inflate(R.layout.error_edittext, this, true)
        editText = root.findViewById(R.id.editText)
        errorText = root.findViewById(R.id.textError)
        btnToggle = root.findViewById(R.id.btnToggle)
        loadAttr(attrs)
    }

    private fun loadAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ErrorEditText)
        val hint = typedArray.getString(R.styleable.ErrorEditText_hint)
        val hideText = typedArray.getBoolean(R.styleable.ErrorEditText_hideText, false)
        typedArray.recycle()
        editText.hint = hint
        if (hideText) {
            hidePasswordText()
        }
    }

    private fun hidePasswordText() {
        editText.setPadding(editText.paddingStart, 0, editText.paddingEnd * 3, 0)
        btnToggle.visibility = View.VISIBLE
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        btnToggle.setOnClickListener {
            if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggle.setImageResource(R.drawable.eye)
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggle.setImageResource(R.drawable.eye_off)
            }
            editText.setSelection(editText.text.length)
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun setErrorMessage(msg: String) {
        editText.setBackgroundResource(R.drawable.rounded_error)
        errorText.text = msg
        errorText.visibility = View.VISIBLE
    }

    fun resetErrorMessage() {
        editText.setBackgroundResource(R.drawable.rounded_searchview)
        errorText.visibility = View.INVISIBLE
    }

    fun getText() = editText.text.toString()
}