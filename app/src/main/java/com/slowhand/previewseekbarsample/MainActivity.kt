package com.slowhand.previewseekbarsample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*

const val SAMPLE_MP4_URL = "https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4"

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
            this@MainActivity,
            DefaultHttpDataSourceFactory(Util.getUserAgent(this@MainActivity, packageName))
        )
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(SAMPLE_MP4_URL))

        return ExoPlayerFactory.newSimpleInstance(this@MainActivity).apply {
            prepare(videoSource)
            playWhenReady = true
        }
    }
}
