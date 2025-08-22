package com.example.favorites.presentation.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.design_system.databinding.ItemPlaylistBinding
import com.example.favorites.domain.model.Playlist

internal class PlaylistsAdapter(
    val onPlaylistClick: (Playlist) -> Unit,
) : PagingDataAdapter<Playlist, PlaylistsAdapter.ViewHolder>(PlaylistDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPlaylistBinding.bind(itemView)
        val parent: ConstraintLayout
        val playlistName: TextView
        val userName: TextView

        init {
            playlistName = binding.textViewPlaylistName
            userName = binding.textViewUsername
            parent = binding.root
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Playlist) {
            playlistName.text = model.name
            userName.text = "${model.user_name} | ${model.creationdate}"

            parent.setOnClickListener {
                onPlaylistClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(com.example.design_system.R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { playlist ->
            holder.bind(playlist)
        }
    }

    private class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}