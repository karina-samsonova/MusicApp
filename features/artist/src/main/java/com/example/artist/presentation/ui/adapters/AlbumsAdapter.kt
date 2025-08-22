package com.example.artist.presentation.ui.adapters

import android.annotation.SuppressLint
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.design_system.databinding.ItemAlbumCellBinding
import com.example.artist.domain.model.Album

internal class AlbumsAdapter(
    val onAlbumClick: (Album) -> Unit,
    private val context: Context
) : PagingDataAdapter<Album, AlbumsAdapter.ViewHolder>(AlbumDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemAlbumCellBinding.bind(itemView)
        val parent: ConstraintLayout
        val photo: ImageView
        val albumName: TextView
        val details: TextView

        init {
            photo = binding.imageViewAlbumCell
            albumName = binding.textViewAlbumName
            details = binding.textViewDetails
            parent = binding.root
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Album) {
            Glide.with(context)
                .load(model.image)
                .placeholder(com.example.design_system.R.drawable.album_holder)
                .transform(RoundedCorners(16))
                .into(photo)
            albumName.text = model.name
            details.text = "${model.artist_name} | ${model.releasedate}"

            parent.setOnClickListener {
                onAlbumClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(com.example.design_system.R.layout.item_album_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumsAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { album ->
            holder.bind(album)
        }
    }

    private class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }
}