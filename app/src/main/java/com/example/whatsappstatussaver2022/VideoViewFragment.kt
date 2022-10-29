package com.example.whatsappstatussaver2022

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class VideoViewFragment : Fragment() {
    var exoPlayer: ExoPlayer?=null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
 val args by navArgs<VideoViewFragmentArgs>()
    lateinit var videoview: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      var view= inflater.inflate(R.layout.fragment_video_view, container, false)
        videoview=view.findViewById(R.id.videoviewexoplayer)


        return  view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility=View.GONE
    }









    fun initializeplayer(){
        exoPlayer=ExoPlayer.Builder(requireContext()).build()

        videoview.player=exoPlayer

        var mediaitem= MediaItem.fromUri(args.status.fileUri)
        exoPlayer?.setMediaItem(mediaitem)
        exoPlayer?.playWhenReady = playWhenReady
        exoPlayer?.seekTo(currentItem, playbackPosition)
        exoPlayer?.prepare()

    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializeplayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((SDK_INT <= 23 || exoPlayer == null)) {
            initializeplayer()
        }

    }

    override fun onPause() {
        super.onPause()

        if (SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()

        if (SDK_INT > 23) {
            releasePlayer()
        }
    }
    private fun releasePlayer() {
        exoPlayer?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        exoPlayer = null
    }
}