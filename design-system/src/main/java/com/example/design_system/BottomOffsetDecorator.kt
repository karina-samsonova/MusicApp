package com.example.design_system

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BottomOffsetDecorator(private val offset: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0

        if (position == itemCount - 1) {
            outRect.bottom = offset
        }
    }
}