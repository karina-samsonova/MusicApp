package com.example.playlist.presentation.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.database.di.DaggerDatabaseComponent
import com.example.database.di.DatabaseComponent
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.network.LoadingState
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import com.example.playlist.R
import com.example.playlist.data.mapper.ExoPlayerTrackMapper
import com.example.playlist.databinding.FragmentPlaylistBinding
import com.example.playlist.di.DaggerPlaylistComponent
import com.example.playlist.di.PlaylistComponent
import com.example.playlist.domain.model.Playlist
import com.example.playlist.domain.model.TrackCell
import com.example.playlist.presentation.ui.adapters.TracksAdapter
import com.example.playlist.presentation.viewModel.PlaylistViewModel
import javax.inject.Inject

class PlaylistFragment @Inject constructor() : Fragment() {
    private var id: String? = null

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        val component = getComponent()
        component.inject(this)
        component.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistBinding.bind(view)

        id = requireArguments().getString("id")
        require(id != null) { "playlistId must not be null" }
        viewModel.getPlaylist(id!!)

        initialiseObservers()
        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): PlaylistComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val databaseComponent: DatabaseComponent =
            DaggerDatabaseComponent.builder().context(requireContext().applicationContext).build()

        val component = DaggerPlaylistComponent.builder().networkComponent(networkComponent)
            .databaseComponent(databaseComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.btnPlay.setOnClickListener {
            onTrackClick(0)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initialiseObservers() {
        viewModel.playlist.observe(viewLifecycleOwner) {
            setUpPlaylistInfo(it)
        }

        viewModel.searchLoadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpPlaylistInfo(playlist: Playlist) {
        binding.textViewName.text = playlist.name
        binding.textViewDescription.text = "${playlist.user_name} | ${playlist.creationdate}"
        setUpTracksRV(playlist.tracks)

        binding.btnFavor.setFavorite(playlist.is_favorite ?: false)

        binding.btnFavor.setOnClickListener {
            if (playlist.is_favorite == true) {
                playlist.is_favorite = false
                viewModel.disfavorPlaylist(playlist.id)
                binding.btnFavor.setFavorite(playlist.is_favorite!!)
            } else {
                playlist.is_favorite = true
                viewModel.favorPlaylist(playlist.id)
                binding.btnFavor.setFavorite(playlist.is_favorite!!)
            }
        }

        binding.btnDownload.setOnClickListener {
            downloadFile(playlist.zip, "${playlist.name}.zip")
        }
    }

    private fun onTrackClick(position: Int) {
        playerViewModel.setPlaylist(
            ExoPlayerTrackMapper().mapListDtoToExoEntity(viewModel.playlist.value?.tracks.orEmpty()),
            position
        )
    }

    private fun setUpTracksRV(tracks: List<TrackCell>) {
        val tracksAdapter = TracksAdapter(::onTrackClick, tracks, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTracks.layoutManager = linearLayoutManager
        binding.rvTracks.adapter = tracksAdapter
    }

    private fun downloadFile(fileUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription(getString(com.example.design_system.R.string.file_download))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.content.visibility = View.VISIBLE
                binding.progressBarSearch.setGone()
            }

            LoadingState.LOADING -> {
                binding.content.visibility = View.GONE
                binding.progressBarSearch.setVisible()
            }

            LoadingState.ERROR -> {
                binding.content.visibility = View.GONE
                binding.progressBarSearch.setGone()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_occurred), Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}