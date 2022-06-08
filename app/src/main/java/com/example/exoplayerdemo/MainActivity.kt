package com.example.exoplayerdemo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayerdemo.databinding.ActivityMainBinding
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
import com.google.android.exoplayer2.PlaybackPreparer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util

class MainActivity : AppCompatActivity(),PlaybackPreparer {
    private lateinit var binding: ActivityMainBinding

    private var trackSelectionParameters : DefaultTrackSelector.Parameters ?= null
    private var player : SimpleExoPlayer ?= null
    private val userAgent :String by lazy{ Util.getUserAgent(this, "ExoPlayerDemo")}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackSelectionParameters = DefaultTrackSelector.ParametersBuilder(this).build()



    }
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory{
        return DefaultHttpDataSourceFactory(userAgent)
    }
    override  fun  preparePlayback ( )  {
        player?.retry()
    }
    private fun initPlayer(){
        val rendererFactory = DefaultRenderersFactory(applicationContext)
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)

        val trackSelector = DefaultTrackSelector(this, AdaptiveTrackSelection.Factory())
        trackSelector.parameters = trackSelectionParameters!!

        player=SimpleExoPlayer.Builder(this,rendererFactory)
            .setTrackSelector(trackSelector)
            .build()
        player?.playWhenReady = true
        binding.playerView.setPlayer(player)
        binding.playerView.setPlaybackPreparer(this)

        val upstreamFactory = DefaultDataSourceFactory(this,buildHttpDataSourceFactory())

        val mediaSource= ProgressiveMediaSource.Factory(upstreamFactory)
            .createMediaSource(Uri.parse("https://html5demos.com/assets/dizzy.mp4"))
        player ?.prepare(mediaSource)
    }

    override fun onResume() {
        super.onResume()
        initPlayer()
    }

    override fun onStop() {
        super.onStop()
        if (player!=null){
            player?.release()
            player=null
        }
    }

}