package com.qltech.messagesaver.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.qltech.messagesaver.common.extensions.playFromPath
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.model.enums.MessageEnum
import com.qltech.ui.view.adapter.AdapterData

class MessagePagerAdapter(
    activity: AppCompatActivity,
    private val player: SimpleExoPlayer = SimpleExoPlayer.Builder(activity).build()
) : PagerAdapter(), DefaultLifecycleObserver, ExoPlayer by player {

    companion object {
        private val TAG = MessagePagerAdapter::class.java.simpleName
    }

    private val lifecycle: Lifecycle = activity.lifecycle
    private var messageList: List<AdapterData.Data<HomeEnum>> = emptyList()
    private var lastVideoPath: String? = null

    init {
        lifecycle.addObserver(this)
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun setList(messageList: List<AdapterData.Data<HomeEnum>>) {
        this.messageList = messageList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return messageList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = getItem(position)

        val content: View = when (item?.getDataType()) {
            HomeEnum.STATUS,
            HomeEnum.LOCAL_STATUS -> {
                val bean: Message = item.getData()
                when (bean.type) {
                    MessageEnum.IMAGE -> getImageView(container, bean.imageUri)
                    MessageEnum.VIDEO -> getVideoView(container)
                }
            }
            else -> View(container.context)
        }

        container.addView(content)
        return content
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as? View ?: return
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return

        val item = getItem(position)?.getData<AdapterData.Data<HomeEnum>>()
        if (view is PlayerView && item is Message && MessageEnum.VIDEO == item.type) {
            if (lastVideoPath != item.path) {
                lastVideoPath = item.path
                view.player = null
                player.stop()
                player.playFromPath(container.context, item.path)
            } else {
                if (!player.isPlaying && !player.isLoading) {
                    player.play()
                }
            }
            view.player = player
        } else {
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    override fun getItemPosition(item: Any): Int {
        return POSITION_NONE
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as? View ?: return

        container.removeView(view)
    }

    fun getItem(position: Int): AdapterData.Data<HomeEnum>? {
        return messageList.getOrNull(position)
    }

    fun indexOf(t: AdapterData.Data<HomeEnum>): Int {
        return messageList.indexOf(t)
    }

    private fun getImageView(container: ViewGroup, imageUrl: String): View {
        val photoView = PhotoView(container.context)
        Glide.with(photoView.context)
            .load(imageUrl)
            .fitCenter()
            .into(photoView)
        return photoView
    }

    private fun getVideoView(container: ViewGroup): View {
        val context = container.context

        return PlayerView(context).apply {
            useController = false
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        player.pause()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
        player.release()
    }

}