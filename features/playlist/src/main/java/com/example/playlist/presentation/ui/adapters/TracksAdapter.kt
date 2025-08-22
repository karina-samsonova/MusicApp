package com.example.playlist.presentation.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.design_system.databinding.ItemTrackBinding
import com.example.playlist.domain.model.TrackCell

internal class TracksAdapter(
    val onTrackClick: (Int) -> Unit,
    private var tracks: List<TrackCell>,
    private val context: Context
) :
    RecyclerView.Adapter<TracksAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemTrackBinding.bind(itemView)
        val parent: ConstraintLayout
        val photo: ImageView
        val trackName: TextView
        val artistName: TextView
        val duration: TextView

        init {
            photo = binding.imageViewTrack
            trackName = binding.textViewTrackName
            artistName = binding.textViewArtistName
            duration = binding.textViewDuration
            parent = binding.root
        }

        @SuppressLint("DefaultLocale")
        fun bind(model: TrackCell, position: Int) {
            Glide.with(context)
                .load(model.image)
                .placeholder(com.example.design_system.R.drawable.track_holder)
                .transform(RoundedCorners(16))
                .into(photo)
            trackName.text = model.name
            artistName.text = model.artist_name
            duration.text = if (model.duration / 3600 > 0) {
                String.format("%02d:%02d:%02d", model.duration / 3600, model.duration / 60 % 60, model.duration % 3600)
            } else {
                String.format("%02d:%02d", model.duration / 60, model.duration % 60)
            }

            parent.setOnClickListener {
                onTrackClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(com.example.design_system.R.layout.item_track, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: TrackCell = tracks[position]
        holder.bind(model, position)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}