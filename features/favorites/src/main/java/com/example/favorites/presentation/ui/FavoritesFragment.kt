package com.example.favorites.presentation.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.example.database.di.DaggerDatabaseComponent
import com.example.database.di.DatabaseComponent
import com.example.design_system.BottomOffsetDecorator
import com.example.design_system.FadeItemAnimator
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.favorites.R
import com.example.favorites.data.mapper.ExoTrackCellsMapper
import com.example.favorites.databinding.FragmentFavoritesBinding
import com.example.favorites.di.DaggerFavoritesComponent
import com.example.favorites.di.FavoritesComponent
import com.example.favorites.domain.model.Album
import com.example.favorites.domain.model.Artist
import com.example.favorites.domain.model.ContentType
import com.example.favorites.domain.model.Playlist
import com.example.favorites.domain.model.TrackCell
import com.example.favorites.presentation.ui.adapters.AlbumsAdapter
import com.example.favorites.presentation.ui.adapters.ArtistsAdapter
import com.example.favorites.presentation.ui.adapters.PlaylistsAdapter
import com.example.favorites.presentation.ui.adapters.TracksAdapter
import com.example.favorites.presentation.viewModel.FavoritesViewModel
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoritesFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    val viewModel: FavoritesViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        val component = getComponent()
        component.inject(this)
        component.inject(viewModel)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): FavoritesComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val databaseComponent: DatabaseComponent =
            DaggerDatabaseComponent.builder().context(requireContext().applicationContext).build()

        val component = DaggerFavoritesComponent.builder().networkComponent(networkComponent)
            .databaseComponent(databaseComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.rvContent.addItemDecoration(BottomOffsetDecorator(120.dpToPx()))
        binding.rvContent.itemAnimator = FadeItemAnimator()

        binding.loadingSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setCurrentAutocomplete(query ?: "")
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.onSearchQuery(query)
                return false
            }
        })

        binding.chipTracks.setOnClickListener {
            setUpTracksListener()
        }
        binding.chipArtists.setOnClickListener {
            setUpArtistsListener()
        }
        binding.chipAlbums.setOnClickListener {
            setUpAlbumsListener()
        }
        binding.chipPlaylists.setOnClickListener {
            setUpPlaylistsListener()
        }

        refreshListeners()
    }

    private fun refreshListeners() {
        when (viewModel.getCurrentContentType()) {
            ContentType.ARTISTS -> setUpArtistsListener()
            ContentType.TRACKS -> setUpTracksListener()
            ContentType.ALBUMS -> setUpAlbumsListener()
            ContentType.PLAYLISTS -> setUpPlaylistsListener()
        }
    }

    private fun showError() {
        binding.rvContent.visibility = View.GONE
        binding.textViewNoContent.visibility = View.GONE
        binding.progressBarSearch.setGone()
        Toast.makeText(
            requireContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG
        ).show()
    }

    private fun showNotLoading(itemCount: Int) {
        binding.rvContent.visibility = View.VISIBLE
        binding.progressBarSearch.setGone()
        if (itemCount == 0) {
            binding.textViewNoContent.visibility = View.VISIBLE
        } else {
            binding.textViewNoContent.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.rvContent.visibility = View.GONE
        binding.textViewNoContent.visibility = View.GONE
        binding.progressBarSearch.setVisible()
    }

    private fun onTrackClick(item: TrackCell, adapter: TracksAdapter) {
        val fullList: List<TrackCell> = adapter.snapshot().items
        val position = fullList.indexOf(item)

        playerViewModel.setPlaylist(
            viewModel.getTrackIds(),
            viewModel.getCurrentAutocomplete(),
            ExoTrackCellsMapper().mapListDtoToEntity(fullList),
            position
        )
    }

    private fun onArtistClick(artist: Artist) {
        val deepLinkUri = "app://com.example.musicapp/artist/${artist.id}"
        findNavController().navigate(Uri.parse(deepLinkUri))
    }

    private fun onAlbumClick(album: Album) {
        val deepLinkUri = "app://com.example.musicapp/album/${album.id}"
        findNavController().navigate(Uri.parse(deepLinkUri))
    }

    private fun onPlaylistClick(playlist: Playlist) {
        val deepLinkUri = "app://com.example.musicapp/playlist/${playlist.id}"
        findNavController().navigate(Uri.parse(deepLinkUri))
    }

    private fun setUpTracksListener() {
        val adapter = TracksAdapter(
            ::onTrackClick, requireContext()
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collectLatest { pagingData ->
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

        viewModel.setCurrentContentType(ContentType.TRACKS)
    }

    private fun Int.dpToPx(): Int {
        return (this * requireContext().resources.displayMetrics.density).toInt()
    }

    private fun setUpAlbumsListener() {
        val adapter = AlbumsAdapter(
            ::onAlbumClick, requireContext()
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.albums.collectLatest { pagingData ->
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

        viewModel.setCurrentContentType(ContentType.ALBUMS)
    }

    private fun setUpArtistsListener() {
        val adapter = ArtistsAdapter(
            ::onArtistClick, requireContext()
        )
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.artists.collectLatest { pagingData ->
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

        viewModel.setCurrentContentType(ContentType.ARTISTS)
    }

    private fun setUpPlaylistsListener() {
        val adapter = PlaylistsAdapter(::onPlaylistClick)
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvContent.layoutManager = linearLayoutManager
        binding.rvContent.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collectLatest { pagingData ->
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

        viewModel.setCurrentContentType(ContentType.PLAYLISTS)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

}