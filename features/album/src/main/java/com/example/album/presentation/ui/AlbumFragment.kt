package com.example.album.presentation.ui

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.album.R
import com.example.album.databinding.FragmentAlbumBinding
import com.example.album.di.AlbumComponent
import com.example.album.di.DaggerAlbumComponent
import com.example.album.domain.model.Album
import com.example.album.presentation.ui.adapters.TracksAdapter
import com.example.album.presentation.viewModel.AlbumViewModel
import com.example.database.di.DaggerDatabaseComponent
import com.example.database.di.DatabaseComponent
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.network.LoadingState
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumFragment @Inject constructor() : Fragment() {
    private var id: String? = null

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    val viewModel: AlbumViewModel by viewModels()

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
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAlbumBinding.bind(view)

        id = requireArguments().getString("id")
        require(id != null) { "albumId must not be null" }
        viewModel.getAlbum(id!!)

        initialiseObservers()
        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): AlbumComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val databaseComponent: DatabaseComponent =
            DaggerDatabaseComponent.builder().context(requireContext().applicationContext).build()

        val component = DaggerAlbumComponent.builder().networkComponent(networkComponent)
            .databaseComponent(databaseComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnPlay.setOnClickListener {
            onTrackClick(0)
        }
    }

    private fun initialiseObservers() {
        viewModel.album.observe(viewLifecycleOwner) {
            setUpAlbumInfo(it)
        }

        viewModel.searchLoadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpAlbumInfo(album: Album) {
        binding.apply {
            textViewName.text = album.name
            textViewDescription.text = "${album.artist_name} | ${album.releasedate}"
            Glide.with(requireContext())
                .load(album.image)
                .placeholder(com.example.design_system.R.drawable.album_holder)
                .transform(RoundedCorners(20))
                .into(imageViewCover)
            setUpTracksListener()

            btnFavor.setFavorite(album.is_favorite ?: false)

            btnFavor.setOnClickListener {
                if (album.is_favorite == true) {
                    album.is_favorite = false
                    viewModel.disfavorAlbum(album.id)
                    btnFavor.setFavorite(album.is_favorite!!)
                } else {
                    album.is_favorite = true
                    viewModel.favorAlbum(album.id)
                    btnFavor.setFavorite(album.is_favorite!!)
                }
            }

            binding.btnDownload.setOnClickListener {
                downloadFile(album.zip, "${album.name}.zip")
            }
        }
    }

    private fun onTrackClick(position: Int) {
        playerViewModel.setPlaylist("", "", id!!, "", "", position, "popularity_total")
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

    private fun setUpTracksListener() {
        val adapter = TracksAdapter(::onTrackClick, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTracks.layoutManager = linearLayoutManager
        binding.rvTracks.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collect { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        showLoading()
                    }

                    is LoadState.NotLoading -> {
                        showNotLoading(adapter.itemCount)
                    }

                    is LoadState.Error -> {
                        showError()
                    }
                }
            }
        }
    }

    private fun showError() {
        binding.rvTracks.visibility = View.GONE
        binding.progressBarContent.setGone()
        Toast.makeText(
            requireContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG
        ).show()
    }

    private fun showNotLoading(itemCount: Int) {
        binding.rvTracks.visibility = View.VISIBLE
        binding.progressBarContent.setGone()
    }

    private fun showLoading() {
        binding.rvTracks.visibility = View.GONE
        binding.progressBarContent.setVisible()
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