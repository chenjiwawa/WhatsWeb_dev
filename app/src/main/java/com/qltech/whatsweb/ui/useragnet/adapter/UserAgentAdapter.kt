package com.qltech.whatsweb.ui.useragnet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.orhanobut.logger.Logger
import com.qltech.whatsweb.R
import com.qltech.firebase.ad.AdManager
import com.qltech.whatsweb.ui.event.UserAgentEvent
import com.qltech.whatsweb.ui.useragnet.model.UserAgent
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import com.qltech.whatsweb.util.RemoteConfigConstants
import kotlinx.android.synthetic.main.item_useragent.view.*
import org.greenrobot.eventbus.EventBus

class UserAgentAdapter(private val list: List<UserAgent>, private var context: Context?) :
    RecyclerView.Adapter<UserAgentAdapter.UserAgentHolder>() {

    class UserAgentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adLayout: FrameLayout = itemView.banner_ad_layout
        val content: TextView = itemView.item_content
        val item: View = itemView.item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAgentHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_useragent, parent, false)
        return UserAgentHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserAgentHolder, position: Int) {
        val currentItem = list[position]
        holder.content.text = currentItem.os + " " + currentItem.browser


        if (position == 2 && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean() && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_BANNER_USERAGENT_ITEM].asBoolean()) {
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = AdManager.getUserAgentBannerId()

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            holder.adLayout.removeAllViews()
            holder.adLayout.addView(adView)
        }

        holder.item.setOnClickListener {
            Logger.d(" setOnClickListener " + currentItem.os + " " + currentItem.browser);
            FirebaseAnalyticsRecoder.getInstance(context)
                .record(
                    FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_USERAGENT,
                    FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SELECT_USERAGENT_ITEM,
                    currentItem.os + "_" + currentItem.browser
                )
            EventBus.getDefault().post(UserAgentEvent(currentItem.userAgent))
        }
    }
}