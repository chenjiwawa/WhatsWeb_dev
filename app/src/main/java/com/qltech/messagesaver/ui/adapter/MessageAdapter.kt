package com.qltech.messagesaver.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.qltech.whatsweb.R
import com.qltech.whatsweb.databinding.LayoutAdMessageGoogleBinding
import com.qltech.firebase.ad.saver.AdManager
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.ui.adapter.holder.MessageLocalViewHolder
import com.qltech.messagesaver.ui.adapter.holder.MessageViewHolder
import com.qltech.ui.view.adapter.AdapterData
import com.qltech.ui.view.adapter.RecyclerAdapterBase
import kotlinx.android.synthetic.main.layout_ad_message_google.view.*

class MessageAdapter : RecyclerAdapterBase<HomeEnum, AdapterData.Data<HomeEnum>>() {

    override val dataTypes: Array<HomeEnum> = HomeEnum.values()

    var onMessageClickListener: OnMessageClickListener? = null

    private var adList: ArrayList<UnifiedNativeAd> = ArrayList()

    init {
        adList.clear()
    }

    fun getAdList(): ArrayList<UnifiedNativeAd> {
        return adList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        anEnum: HomeEnum
    ): ViewHolder<AdapterData.Data<HomeEnum>>? {
        return when (anEnum) {
            HomeEnum.STATUS -> {
                MessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(MessageViewHolder.LAYOUT_RES, parent, false)
                ).also {
                    it.onMessageClickListener = onMessageClickListener
                }
            }
            HomeEnum.LOCAL_STATUS -> {
                MessageLocalViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(MessageLocalViewHolder.LAYOUT_RES, parent, false)
                ).also {
                    it.onMessageClickListener = onMessageClickListener
                }
            }
            HomeEnum.AD_STATUS -> {
                AdMessageViewHolder(getAdMobView(parent))
            }
        } as? ViewHolder<AdapterData.Data<HomeEnum>>
    }

    private fun getAdMobView(container: ViewGroup): View {
        val adBinding =
            DataBindingUtil.inflate<LayoutAdMessageGoogleBinding>(
                LayoutInflater.from(container.context),
                R.layout.layout_ad_message_google,
                container,
                false
            )
        val adLoader = AdLoader.Builder(container.context, AdManager.getMessageListId())
            .forUnifiedNativeAd { unifiedNativeAd ->
                if (unifiedNativeAd.headline == null) {
                    adBinding.adContent.title.visibility = View.INVISIBLE
                } else {
                    adBinding.adContent.title.visibility = View.VISIBLE
                    adBinding.adContent.title.text = unifiedNativeAd.headline
                    adBinding.adContent.title.background =
                        ContextCompat.getDrawable(container.context, R.color.main)
                    adBinding.unified.headlineView = adBinding.adContent.title
                }
                if (unifiedNativeAd.body == null) {
                    adBinding.adContent.body.visibility = View.INVISIBLE
                } else {
                    adBinding.adContent.body.visibility = View.VISIBLE
                    adBinding.adContent.body.text = unifiedNativeAd.body
                    adBinding.adContent.body.background =
                        ContextCompat.getDrawable(container.context, R.color.main)
                    adBinding.unified.bodyView = adBinding.adContent.body
                }
                if (unifiedNativeAd.icon == null) {
                    adBinding.adContent.icon.visibility = View.GONE
                } else {
                    adBinding.adContent.icon.visibility = View.VISIBLE
                    adBinding.adContent.icon.setImageDrawable(unifiedNativeAd.icon.drawable)
                    adBinding.unified.iconView = adBinding.adContent.icon
                }
                adBinding.adContent.mediaLayout.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                adBinding.adContent.mediaLayout.setMediaContent(unifiedNativeAd.mediaContent)
                adBinding.unified.mediaView = adBinding.adContent.mediaLayout

                (adBinding.root.unified as UnifiedNativeAdView).setNativeAd(unifiedNativeAd)
                adList.add(unifiedNativeAd)
            }.build()
        adLoader.loadAd(AdRequest.Builder().build())
        return adBinding.root
    }

    interface OnMessageClickListener {
        fun onDownloadBtnClick(message: Message)
        fun onShareBtnClick(message: Message)
        fun onSelected(message: Message, isSelected: Boolean)
    }

}

class AdMessageViewHolder(itemView: View) : RecyclerAdapterBase.ViewHolder<Message>(itemView)