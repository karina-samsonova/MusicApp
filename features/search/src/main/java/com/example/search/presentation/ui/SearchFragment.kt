package com.example.search.presentation.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
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
import com.example.design_system.BottomOffsetDecorator
import com.example.design_system.FadeItemAnimator
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.network.LoadingState
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import com.example.search.R
import com.example.search.data.mapper.ExoTrackCellsMapper
import com.example.search.databinding.FragmentSearchBinding
import com.example.search.di.DaggerSearchComponent
import com.example.search.di.SearchComponent
import com.example.search.domain.model.Album
import com.example.search.domain.model.Artist
import com.example.search.domain.model.ContentType
import com.example.search.domain.model.Playlist
import com.example.search.domain.model.TrackCell
import com.example.search.presentation.ui.adapters.AlbumsAdapter
import com.example.search.presentation.ui.adapters.ArtistsAdapter
import com.example.search.presentation.ui.adapters.PlaylistsAdapter
import com.example.search.presentation.ui.adapters.TracksAdapter
import com.example.search.presentation.viewModel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    val viewModel: SearchViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        val component = getComponent()
        component.inject(this)
        component.inject(viewModel)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        initialiseObservers()
        initialiseUIElements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): SearchComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val component = DaggerSearchComponent.builder().networkComponent(networkComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.rvContent.addItemDecoration(BottomOffsetDecorator(120.dpToPx()))
        binding.rvContent.itemAnimator = FadeItemAnimator()

        binding.loadingSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                setAutocomplete(query ?: "")
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.onSearchQuery(query)
                return false
            }
        })

        binding.chipTracks.setOnClickListener {
            setUpTracksListener()
            viewModel.setCurrentContentType(ContentType.TRACKS)
        }
        binding.chipArtists.setOnClickListener {
            setUpArtistsListener()
            viewModel.setCurrentContentType(ContentType.ARTISTS)
        }
        binding.chipAlbums.setOnClickListener {
            setUpAlbumsListener()
            viewModel.setCurrentContentType(ContentType.ALBUMS)
        }
        binding.chipPlaylists.setOnClickListener {
            setUpPlaylistsListener()
            viewModel.setCurrentContentType(ContentType.PLAYLISTS)
        }

        refreshListeners()
    }

    private fun initialiseObservers() {
        viewModel.searchContent()

        viewModel.autocompleteLoadingStateLiveData.observe(viewLifecycleOwner) {
            onAutocompleteLoadingStateChanged(it)
        }

        viewModel.autocompletes.observe(viewLifecycleOwner) {
            setUpAutocompleteSuggestions(it)
        }
    }

    private fun refreshListeners() {
        when (viewModel.getCurrentContentType()) {
            ContentType.ARTISTS -> setUpArtistsListener()
            ContentType.TRACKS -> setUpTracksListener()
            ContentType.ALBUMS -> setUpAlbumsListener()
            ContentType.PLAYLISTS -> setUpPlaylistsListener()
        }
    }

    private fun setAutocomplete(query: String) {
        viewModel.setCurrentAutocomplete(query)
        refreshListeners()
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

    private fun setUpAutocompleteSuggestions(autocompletes: List<String>) {
        val cursorAdapter = SimpleCursorAdapter(
            requireContext(),
            com.example.design_system.R.layout.item_autocomplete,
            null,
            arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1),
            intArrayOf(com.example.design_system.R.id.match),
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        binding.loadingSearchView.setSuggestionsAdapter(cursorAdapter)

        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
        autocompletes.forEachIndexed { index, match ->
            cursor.addRow(arrayOf(index, match))
        }
        cursorAdapter.changeCursor(cursor)
        binding.loadingSearchView.requestSearchViewFocus()

        binding.loadingSearchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor =
                    binding.loadingSearchView.getSuggestionsAdapter().getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                binding.loadingSearchView.setQuery(selection, false)

                onAutocompleteClick(autocompletes[position])

                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }
        })
    }

    private fun onAutocompleteClick(autocomplete: String) {
        setAutocomplete(autocomplete)
    }

    private fun onTrackClick(item: TrackCell, adapter: TracksAdapter) {
        val fullList: List<TrackCell> = adapter.snapshot().items
        val position = fullList.indexOf(item)

        playerViewModel.setPlaylist(
            "",
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
        val adapter = TracksAdapter(::onTrackClick, requireContext())
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

    private fun setUpAlbumsListener() {
        val adapter = AlbumsAdapter(::onAlbumClick, requireContext())
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
        val adapter = ArtistsAdapter(::onArtistClick, requireContext())
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

    private fun Int.dpToPx(): Int {
        return (this * requireContext().resources.displayMetrics.density).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    private fun onAutocompleteLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.loadingSearchView.setLoading(false)
                binding.loadingSearchView.requestSearchViewFocus()
            }

            LoadingState.LOADING -> {
                binding.loadingSearchView.setLoading(true)
            }

            LoadingState.ERROR -> {
                binding.loadingSearchView.setLoading(false)
                binding.loadingSearchView.requestSearchViewFocus()
                Toast.makeText(
                    requireContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}