package com.example.artist.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.artist.R
import com.example.artist.databinding.FragmentArtistBinding
import com.example.artist.di.ArtistComponent
import com.example.artist.di.DaggerArtistComponent
import com.example.artist.domain.model.Album
import com.example.artist.domain.model.Artist
import com.example.artist.domain.model.ContentType
import com.example.artist.presentation.ui.adapters.AlbumsAdapter
import com.example.artist.presentation.ui.adapters.TracksAdapter
import com.example.artist.presentation.viewModel.ArtistViewModel
import com.example.database.di.DaggerDatabaseComponent
import com.example.database.di.DatabaseComponent
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.network.LoadingState
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArtistFragment @Inject constructor() : Fragment() {
    private var id: String? = null

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    val viewModel: ArtistViewModel by viewModels()

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
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArtistBinding.bind(view)

        id = requireArguments().getString("id")
        require(id != null) { "artistId must not be null" }
        viewModel.getArtist(id!!)

        initialiseObservers()
        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): ArtistComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val databaseComponent: DatabaseComponent =
            DaggerDatabaseComponent.builder().context(requireContext().applicationContext).build()

        val component = DaggerArtistComponent.builder().networkComponent(networkComponent)
            .databaseComponent(databaseComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.tabMenu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == binding.tabMenu.getTabAt(0)) {
                    setUpTracksListener()
                    viewModel.setCurrentContentType(ContentType.TRACKS, id!!)
                } else {
                    setUpAlbumsListener()
                    viewModel.setCurrentContentType(ContentType.ALBUMS, id!!)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        refreshListeners()
    }

    private fun initialiseObservers() {
        viewModel.artist.observe(viewLifecycleOwner) {
            setUpArtistInfo(it)
        }

        viewModel.searchLoadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    private fun refreshListeners() {
        when (viewModel.getCurrentContentType()) {
            ContentType.TRACKS -> setUpTracksListener()
            ContentType.ALBUMS -> setUpAlbumsListener()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpArtistInfo(artist: Artist) {
        binding.textViewName.text = artist.name
        Glide.with(requireContext())
            .load(artist.image)
            .placeholder(com.example.design_system.R.drawable.artist_holder)
            .transform(RoundedCorners(20))
            .into(binding.imageViewCover)

        binding.btnFavor.setFavorite(artist.is_favorite ?: false)

        binding.btnFavor.setOnClickListener {
            if (artist.is_favorite == true) {
                artist.is_favorite = false
                viewModel.disfavorArtist(artist.id)
                binding.btnFavor.setFavorite(artist.is_favorite!!)
            } else {
                artist.is_favorite = true
                viewModel.favorArtist(artist.id)
                binding.btnFavor.setFavorite(artist.is_favorite!!)
            }
        }
    }

    private fun onTrackClick(position: Int) {
        playerViewModel.setPlaylist("", id!!, "", "", "", position, "popularity_total")
    }

    private fun onAlbumClick(album: Album) {
        val deepLinkUri = "app://com.example.musicapp/album/${album.id}"
        findNavController().navigate(Uri.parse(deepLinkUri))
    }

    private fun setUpTracksListener() {
        val adapter = TracksAdapter(::onTrackClick, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

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

        viewModel.setCurrentContentType(ContentType.TRACKS, id!!)
    }

    private fun setUpAlbumsListener() {
        val adapter = AlbumsAdapter(::onAlbumClick, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.albums.collect { pagingData ->
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

        viewModel.setCurrentContentType(ContentType.ALBUMS, id!!)
    }

    private fun showError() {
        binding.rvContent.visibility = View.GONE
        binding.progressBarContent.setGone()
        Toast.makeText(
            requireContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG
        ).show()
    }

    private fun showNotLoading(itemCount: Int) {
        binding.rvContent.visibility = View.VISIBLE
        binding.progressBarContent.setGone()
    }

    private fun showLoading() {
        binding.rvContent.visibility = View.GONE
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