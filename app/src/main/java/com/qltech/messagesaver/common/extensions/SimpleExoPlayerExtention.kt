package com.qltech.messagesaver.common.extensions

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.qltech.whatsweb.BuildConfig

fun SimpleExoPlayer.playFromPath(context: Context, path: String) {
    val videoSource: MediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context,
        Util.getUserAgent(context, BuildConfig.APPLICATION_ID)))
        .createMediaSource(MediaItem.fromUri(path))
    setMediaSource(videoSource)
    seekTo(0)
    prepare()
}