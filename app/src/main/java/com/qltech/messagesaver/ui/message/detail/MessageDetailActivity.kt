package com.qltech.messagesaver.ui.message.detail

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.qltech.common.args.ArgsCreator
import com.qltech.common.extensions.subscribe
import com.qltech.common.extensions.visible
import com.qltech.common.utils.IntentUtils
import com.qltech.whatsweb.BuildConfig
import com.qltech.whatsweb.R
import com.qltech.messagesaver.arguments.MessageDetailArguments
import com.qltech.messagesaver.common.utils.WhatsAppUtils
import com.qltech.firebase.ad.saver.AdManager
import com.qltech.firebase.analytics.FirebaseAnalyticHelper
import com.qltech.firebase.analytics.FirebaseEvent
import com.qltech.firebase.helper.setOnClickTouchListener
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.model.enums.MessageEnum
import com.qltech.messagesaver.ui.adapter.MessagePagerAdapter
import com.qltech.messagesaver.viewmodel.IMessageDetailViewModel
import com.qltech.messagesaver.viewmodel.MessageDetailViewModel
import com.qltech.ui.BaseActivity
import com.qltech.ui.helper.LoadingAdditional
import com.qltech.ui.helper.SnackBarAdditional
import com.qltech.ui.model.SnackMessage
import kotlinx.android.synthetic.main.activity_message_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class MessageDetailActivity : BaseActivity(R.layout.activity_message_detail), LoadingAdditional,
    SnackBarAdditional {

    companion object {
        private val TAG = MessageDetailActivity::class.java.simpleName
    }

    private val arguments: MessageDetailArguments by ArgsCreator()

    private val viewModel: IMessageDetailViewModel by viewModel<MessageDetailViewModel> {
        parametersOf(
            arguments
        )
    }

    private val adapter: MessagePagerAdapter by lazy { MessagePagerAdapter(this) }
    private var playHandler: PlayHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {

        group_video_control.referencedIds = intArrayOf(R.id.play_btn,R.id.seek_bar_group)
        group_control.referencedIds = intArrayOf(R.id.tool_bar,R.id.bottom_bar)
        message_pager.adapter = adapter
        setActionBarTitle("")
        setDisplayHomeAsUpEnabled(true)
        playHandler = PlayHandler(video_seek_bar, video_time_current, adapter)

        message_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                onMessageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        text_send.setOnClickListener {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.STATUS_SEND)
            val message = viewModel.currentMessage.value ?: return@setOnClickListener

            IntentUtils.shareImage(
                this,
                File(message.path),
                "${BuildConfig.APPLICATION_ID}.provider",
                WhatsAppUtils.WHATS_APP_PKG_NAME
            )
        }
        text_share.setOnClickListener {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.STATUS_SHARE)

            val message = viewModel.currentMessage.value ?: return@setOnClickListener

            IntentUtils.shareImage(
                this,
                File(message.path),
                "${BuildConfig.APPLICATION_ID}.provider",
                null
            )
        }
        text_download.setOnClickListener {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.STATUS_DOWNLOAD)
            val message = viewModel.currentMessage.value ?: return@setOnClickListener

            permissionHelper.runOnPermissionGranted(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                if (it) {
                    viewModel.downloadMessage(message)
                } else {
                    showSnackBar(
                        SnackMessage(
                            SnackMessage.Type.ERROR,
                            getString(R.string.need_permission_to_work)
                        )
                    )
                }
            }
        }
        text_delete.setOnClickListener {
            val message = viewModel.currentMessage.value ?: return@setOnClickListener

            deleteMessage(message)
        }
        play_btn.setOnClickListener {
            if (adapter.isPlaying) {
                adapter.pause()
            } else {
                adapter.play()
            }
        }
        message_pager.setOnClickTouchListener {
//            group_control.visible = !group_control.visible
            group_video_control.visible =
                MessageEnum.VIDEO == viewModel.currentMessage.value?.type && group_control.visible
        }
        video_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    adapter.seekTo(progress.toLong())
                    video_time_current.text = convertDuration(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                adapter.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                adapter.seekTo(seekBar.progress.toLong())
                adapter.play()
            }

        })
        adapter.addListener(object : Player.EventListener {


            override fun onPlaybackStateChanged(state: Int) {
                if (STATE_READY == state) {
                    video_time_max.text = convertDuration(adapter.duration)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    play_btn.setImageResource(R.drawable.ic_vedio_pause)
                    playHandler?.start()
                } else {
                    play_btn.setImageResource(R.drawable.ic_vedio_play)
                    playHandler?.stop()
                }
            }

        })

        initBannerAd()
    }

    private fun initBannerAd() {
        if (AdRemoteConfig.getAdSwitch()) {
            val adView = AdView(this)
            adView.adSize = adSize
            adView.adUnitId = AdManager.getMessageDetailBannerId()

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            banner_ad_layout.addView(adView)
        }
    }

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = banner_ad_layout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun initData() {
        bindLoading(viewModel.loadingLiveData)
        bindSnackBar(viewModel.snackBarMessageLiveData)

        subscribe(viewModel.currentMessage) {
            text_download.visible = !it.isSaved
            text_delete.visible = it.isSaved
        }
        subscribe(viewModel.messageList) { list ->
            if (list.isEmpty()) {
                finish()
                return@subscribe
            }

            adapter.setList(list)
            val newIndex = list.indexOfFirst {
                it is com.qltech.messagesaver.model.Message && it.name == viewModel.currentMessage.value?.name
            }.takeIf { it >= 0 }

            if (null != newIndex) {
                message_pager.currentItem = newIndex
            } else {
                onMessageSelected(message_pager.currentItem)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onMessageSelected(position: Int) {
        val selectedItem = adapter.getItem(position)
        selectedItem?.takeIf { HomeEnum.STATUS == it.getDataType() || HomeEnum.LOCAL_STATUS == it.getDataType() }
            ?.getData<com.qltech.messagesaver.model.Message>()
            ?.run(viewModel::onMessageSelected)

        group_video_control.visible =
            group_control.visible && MessageEnum.VIDEO == (selectedItem as? com.qltech.messagesaver.model.Message)?.type
    }

    private fun deleteMessage(message: com.qltech.messagesaver.model.Message) {
        permissionHelper.runOnPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            if (it) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_delete_title)
                    .setPositiveButton(R.string.delete_it) { _, _ ->
                        FirebaseAnalyticHelper.logEvent(FirebaseEvent.STATUS_DELETE)
                        viewModel.removeMessage(message)
                    }
                    .show()
            } else {
                showSnackBar(
                    SnackMessage(
                        SnackMessage.Type.ERROR,
                        getString(R.string.need_permission_to_work)
                    )
                )
            }
        }
    }

    class PlayHandler(
        private val seekBar: SeekBar,
        private val videoTimeCurrent: TextView,
        private val player: ExoPlayer
    ) : Handler(Looper.getMainLooper()) {

        fun start() {
            sendEmptyMessage(0)
        }

        fun stop() {
            removeMessages(0)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    seekBar.max = player.duration.toInt()
                    seekBar.progress = player.currentPosition.toInt()
                    videoTimeCurrent.text = convertDuration(player.currentPosition)
                    sendEmptyMessageDelayed(0, 30)
                }
            }
        }
    }

}

private fun convertDuration(duration: Long): String? {
    val hours: Long = duration / (1000 * 60 * 60)
    val minutes = ((duration % (1000 * 60 * 60)) / (1000 * 60))
    val seconds = ((duration % (1000 * 60)) / 1000)
    return if (hours > 0) {
        "$hours:%02d:%02d".format(minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}