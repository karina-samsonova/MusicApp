package com.example.home.presentation.ui

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.home.R
import com.example.home.data.mapper.ExoPlayerTrackMapper
import com.example.home.databinding.FragmentHomeBinding
import com.example.home.di.DaggerHomeComponent
import com.example.home.di.HomeComponent
import com.example.home.domain.model.TrackCell
import com.example.home.presentation.ui.adapters.TracksAdapter
import com.example.home.presentation.viewModel.HomeViewModel
import com.example.network.LoadingState
import com.example.network.di.DaggerNetworkComponent
import com.example.network.di.NetworkComponent
import javax.inject.Inject

class HomeFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        val component = getComponent()
        component.inject(this)
        component.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        initialiseUIElements()
        initialiseObservers()
    }

    private fun initialiseObservers() {
        viewModel.getNewTracks()

        viewModel.tracks.observe(viewLifecycleOwner) {
            setUpTracksRV(it)
        }

        viewModel.searchLoadingStateLiveData.observe(viewLifecycleOwner) {
            onLoadingStateChanged(it)
        }
    }

    private fun setUpTracksRV(tracks: List<TrackCell>) {
        val tracksAdapter = TracksAdapter(::onTrackClick, tracks, requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.newTracks.layoutManager = linearLayoutManager
        binding.newTracks.adapter = tracksAdapter
    }

    private fun onTrackClick(position: Int) {
        playerViewModel.setPlaylist(
            ExoPlayerTrackMapper().mapListDtoToExoEntity(viewModel.tracks.value.orEmpty()),
            position
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getComponent(): HomeComponent {
        val networkComponent: NetworkComponent = DaggerNetworkComponent.builder().build()

        val component = DaggerHomeComponent.builder().networkComponent(networkComponent).build()

        return component
    }

    private fun initialiseUIElements() {
        binding.apply {
            btnSettings.setOnClickListener {
                val deepLinkUri = "app://com.example.musicapp/settings"
                findNavController().navigate(Uri.parse(deepLinkUri))
            }
            btnPlay.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "", 1, "popularity_week")
            }
            btnFun.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "fun", 0, "relevance")
            }
            btnSad.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "sad", 0, "relevance")
            }
            btnLove.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "love", 0, "relevance")
            }
            btnCalm.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "calm", 0, "relevance")
            }
            btnRock.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "rock", 0, "relevance")
            }
            btnJazz.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "jazz", 0, "relevance")
            }
            btnPop.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "pop", 0, "relevance")
            }
            btnElectro.setOnClickListener {
                playerViewModel.setPlaylist("", "", "", "", "electro", 0, "relevance")
            }
        }
    }

    private fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.SUCCESS -> {
                binding.newTracks.visibility = View.VISIBLE
                binding.progressBarSearch.setGone()
            }

            LoadingState.LOADING -> {
                binding.newTracks.visibility = View.INVISIBLE
                binding.progressBarSearch.setVisible()
            }

            LoadingState.ERROR -> {
                binding.newTracks.visibility = View.GONE
                binding.progressBarSearch.setGone()
                Toast.makeText(
                    requireContext(),
                    getString(com.example.design_system.R.string.error_occurred), Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}