package com.example.musicapp.presentation

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.design_system.FavorIcon
import com.example.design_system.PlayIcon
import com.example.design_system.RepeatIcon
import com.example.design_system.ShuffleIcon
import com.example.exoplayer.domain.model.TrackCell
import com.example.exoplayer.presentation.viewModel.PlayerViewModel
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentMainBinding
import com.example.musicapp.di.App
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

class MainFragment @Inject constructor() : Fragment() {
    private lateinit var gestureDetector: GestureDetector

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val playerViewModel: PlayerViewModel by activityViewModels(
        factoryProducer = { (requireActivity() as MainActivity).playerViewModelFactory }
    )

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var progressUpdateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigation()
        initialiseUIElements()
        initialiseObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.bottom_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavMenu.setupWithNavController(navController)
    }

    private fun initialiseUIElements() {
        gestureDetector = GestureDetector(requireContext(), HorizontalSwipeListener(
            onSwipeLeft = { playerViewModel.nextTrack() },
            onSwipeRight = { playerViewModel.previousTrack() }
        ))

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playerSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> showFullPlayer()
                        BottomSheetBehavior.STATE_COLLAPSED -> showMiniPlayer()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        requireActivity().findViewById<ConstraintLayout>(R.id.miniPlayerRoot).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun initialiseObservers() {
        playerViewModel.currentTrack.observe(viewLifecycleOwner) {
            if (it != null) {
                setUpMiniPlayer(it)
                setUpFullPlayer(it)
            }
        }

        playerViewModel.playlistUpdated.observe(viewLifecycleOwner) {
            binding.playerSheet.visibility = View.VISIBLE
            showMiniPlayer()
        }

        playerViewModel.isPlaying.observe(viewLifecycleOwner) {
            requireActivity().findViewById<PlayIcon>(R.id.miniPlayerBtnPlay).setPlaying(it)
            requireActivity().findViewById<PlayIcon>(R.id.fullPlayerBtnPlay).setPlaying(it)
        }
    }

    private fun setUpMiniPlayer(track: TrackCell) {
        Glide.with(this)
            .load(track.image)
            .placeholder(com.example.design_system.R.drawable.track_holder)
            .transform(RoundedCorners(16))
            .into(requireActivity().findViewById(R.id.miniPlayerCover))
        requireActivity().findViewById<TextView>(R.id.miniPlayerTrackName).text = track.name
        requireActivity().findViewById<TextView>(R.id.miniPlayerArtistName).text = track.artist_name

        val btnFavor = requireActivity().findViewById<FavorIcon>(R.id.miniPlayerBtnFavor)
        btnFavor.setFavorite(track.is_favorite)
        btnFavor.setOnClickListener {
            track.is_favorite = playerViewModel.favorOrDisfavorTrack(track)
            btnFavor.doOnClick(track.is_favorite)
            requireActivity().findViewById<FavorIcon>(R.id.fullPlayerBtnFavor)
                .setFavorite(track.is_favorite)
        }

        requireActivity().findViewById<PlayIcon>(R.id.miniPlayerBtnPlay).setOnClickListener {
            playerViewModel.playOrPauseTrack()
        }
    }

    private fun setUpFullPlayer(track: TrackCell) {
        Glide.with(this)
            .load(track.image)
            .placeholder(com.example.design_system.R.drawable.track_holder)
            .transform(RoundedCorners(20))
            .into(requireActivity().findViewById(R.id.fullPlayerCover))
        requireActivity().findViewById<TextView>(R.id.fullPlayerName).text = track.name
        requireActivity().findViewById<TextView>(R.id.fullPlayerArtistName).text = track.artist_name

        requireActivity().findViewById<ImageView>(R.id.fullPlayerBtnNext).setOnClickListener {
            playerViewModel.nextTrack()
        }
        requireActivity().findViewById<ImageView>(R.id.fullPlayerBtnPrevious).setOnClickListener {
            playerViewModel.previousTrack()
        }

        val btnFavor = requireActivity().findViewById<FavorIcon>(R.id.fullPlayerBtnFavor)
        btnFavor.setFavorite(track.is_favorite)
        btnFavor.setOnClickListener {
            track.is_favorite = playerViewModel.favorOrDisfavorTrack(track)
            btnFavor.doOnClick(track.is_favorite)
            requireActivity().findViewById<FavorIcon>(R.id.miniPlayerBtnFavor)
                .setFavorite(track.is_favorite)
        }

        if (track.audiodownload_allowed) {
            val btnDownload = requireActivity().findViewById<ImageView>(R.id.fullPlayerBtnDownload)
            btnDownload.visibility = View.VISIBLE
            btnDownload.setOnClickListener {
                downloadFile(track.audiodownload, "${track.name}.mp3")
            }
        }

        requireActivity().findViewById<PlayIcon>(R.id.fullPlayerBtnPlay).setOnClickListener {
            playerViewModel.playOrPauseTrack()
        }

        val btnShuffle = requireActivity().findViewById<ShuffleIcon>(R.id.fullPlayerBtnShuffle)
        btnShuffle.setIsEnabled(playerViewModel.getShuffleMode())
        btnShuffle.setOnClickListener {
            btnShuffle.setIsEnabled(playerViewModel.changeShuffleMode())
        }

        val btnRepeat = requireActivity().findViewById<RepeatIcon>(R.id.fullPlayerBtnRepeat)
        when (playerViewModel.getRepeatMode()) {
            Player.REPEAT_MODE_OFF -> btnRepeat.setRepeatOff()
            Player.REPEAT_MODE_ONE -> btnRepeat.setRepeatOne()
            Player.REPEAT_MODE_ALL -> btnRepeat.setRepeatAll()
        }
        btnRepeat.setOnClickListener {
            when (playerViewModel.changeRepeatMode()) {
                Player.REPEAT_MODE_OFF -> btnRepeat.setRepeatOff()
                Player.REPEAT_MODE_ONE -> btnRepeat.setRepeatOne()
                Player.REPEAT_MODE_ALL -> btnRepeat.setRepeatAll()
            }
        }
    }

    private fun setupSeekBar(seekBar: SeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playerViewModel.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                progressUpdateJob?.cancel()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                startProgressUpdates(seekBar)
            }
        })

        startProgressUpdates(seekBar)
    }

    private fun startProgressUpdates(seekBar: SeekBar) {
        progressUpdateJob?.cancel()
        progressUpdateJob = lifecycleScope.launch {
            while (true) {
                withContext(Dispatchers.Main) {
                    seekBar.max = playerViewModel.duration()
                    seekBar.progress = playerViewModel.currentPosition()
                }
                delay(1000)
            }
        }
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

    @SuppressLint("ClickableViewAccessibility")
    private fun showFullPlayer() {
        requireActivity().findViewById<ConstraintLayout>(R.id.miniPlayerRoot).visibility = View.GONE
        val fullRoot = requireActivity().findViewById<ConstraintLayout>(R.id.trackRoot)
        fullRoot.visibility = View.VISIBLE
        fullRoot.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        binding.bottomNavMenu.visibility = View.GONE
        setupSeekBar(requireActivity().findViewById(R.id.fullPlayerSeekBar))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showMiniPlayer() {
        val paddingBottom = 70.dpToPx()
        requireActivity().findViewById<FragmentContainerView>(R.id.bottom_nav_host)
            .setPadding(0, 0, 0, paddingBottom)

        requireActivity().findViewById<ConstraintLayout>(R.id.trackRoot).visibility = View.GONE
        val miniRoot = requireActivity().findViewById<ConstraintLayout>(R.id.miniPlayerRoot)
        miniRoot.visibility = View.VISIBLE
        miniRoot.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        binding.bottomNavMenu.visibility = View.VISIBLE
        setupSeekBar(requireActivity().findViewById(R.id.miniPlayerSeekBar))
    }

    private fun Int.dpToPx(): Int {
        return (this * requireContext().resources.displayMetrics.density).toInt()
    }

    private inner class HorizontalSwipeListener(
        val onSwipeLeft: () -> Unit,
        val onSwipeRight: () -> Unit,
    ) : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return true
                }
            }
            return false
        }
    }
}