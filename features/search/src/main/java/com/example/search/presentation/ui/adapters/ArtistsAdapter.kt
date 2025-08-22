package com.example.search.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.design_system.databinding.ItemArtistBinding
import com.example.search.domain.model.Artist

internal class ArtistsAdapter(
    val onArtistClick: (Artist) -> Unit,
    private val context: Context
) : PagingDataAdapter<Artist, ArtistsAdapter.ViewHolder>(ArtistDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemArtistBinding.bind(itemView)
        val parent: ConstraintLayout
        val photo: ImageView
        val artistName: TextView

        init {
            photo = binding.imageViewArtist
            artistName = binding.textViewArtistName
            parent = binding.root
        }

        fun bind(model: Artist) {
            Glide.with(context)
                .load(model.image)
                .placeholder(com.example.design_system.R.drawable.artist_holder)
                .circleCrop()
                .into(photo)
            artistName.text = model.name

            parent.setOnClickListener {
                onArtistClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(com.example.design_system.R.layout.item_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistsAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { artist ->
            holder.bind(artist)
        }
    }

    private class ArtistDiffCallback : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }
    }
}