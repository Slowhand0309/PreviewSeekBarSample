package com.slowhand.previewseekbarsample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.github.rubensousa.previewseekbar.PreviewView
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import kotlinx.android.synthetic.main.controller_view.*

const val SAMPLE_MP4_URL = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4"
const val TOTAL_VIDEO_MSEC = 117000

val DUMMY_THUMBNAILS = listOf(
    "https://placehold.jp/a6eaf5/0a0909/150x150.png",
    "https://placehold.jp/f5baa6/0a0909/150x150.png",
    "https://placehold.jp/abf5a6/0a0909/150x150.png",
    "https://placehold.jp/e0a6f5/0a0909/150x150.png"
)

class MainActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // フルスクリーンにする
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }

        // PreviewTimeBarを取得
        val previewTimeBar = playerView.findViewById<PreviewTimeBar>(R.id.exo_progress)
        previewTimeBar.addOnPreviewChangeListener(onPreviewChangeListener)
    }

    // シークバーをDrag&Dropした時に呼ばれるListener
    private val onPreviewChangeListener = object : PreviewView.OnPreviewChangeListener {
        override fun onPreview(previewView: PreviewView?, progress: Int, fromUser: Boolean) {
            val eachTime = TOTAL_VIDEO_MSEC / DUMMY_THUMBNAILS.size
            val index = progress / eachTime
            val url = DUMMY_THUMBNAILS.elementAtOrNull(index) ?: return
            // progressの位置に応じたサムネイルを表示
            Glide.with(this@MainActivity)
                .load(url)
                .into(imageView)
        }

        override fun onStartPreview(previewView: PreviewView?, progress: Int) {
        }

        override fun onStopPreview(previewView: PreviewView?, progress: Int) {
        }
    }

    override fun onStart() {
        super.onStart()
        player = createPlayer()
        playerView.player = player
    }

    override fun onStop() {
        super.onStop()
        player?.release()
    }


    private fun createPlayer(): ExoPlayer {
        val dataSourceFactory = DefaultDataSourceFactory(
            this@MainActivity, DefaultBandwidthMeter(),
            DefaultHttpDataSourceFactory(Util.getUserAgent(this@MainActivity, packageName))
        )
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(SAMPLE_MP4_URL))

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        return ExoPlayerFactory.newSimpleInstance(this@MainActivity, trackSelector).apply {
            prepare(videoSource)
            playWhenReady = true
        }
    }
}
