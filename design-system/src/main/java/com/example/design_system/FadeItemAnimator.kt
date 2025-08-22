package com.example.design_system

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class FadeItemAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.apply {
            alpha = 0f
            animate().alpha(1f).setDuration(300).start()
        }
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.apply {
            animate().alpha(0f).setDuration(300).withEndAction {
                alpha = 1f
            }.start()
        }
        return super.animateRemove(holder)
    }
}