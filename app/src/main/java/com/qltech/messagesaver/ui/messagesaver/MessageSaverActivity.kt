package com.qltech.messagesaver.ui.messagesaver

import android.Manifest
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.qltech.common.args.putArgs
import com.qltech.common.extensions.subscribe
import com.qltech.base.helper.AppHelper
import com.qltech.common.utils.IntentUtils
import com.qltech.whatsweb.R
import com.qltech.messagesaver.arguments.MessageArguments
import com.qltech.whatsweb.databinding.ActivityMessageSaverBinding
import com.qltech.firebase.ad.saver.AdManager
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.messagesaver.model.enums.MessageSourceEnum
import com.qltech.messagesaver.ui.message.MessageFragment
import com.qltech.messagesaver.viewmodel.IMainViewModel
import com.qltech.messagesaver.viewmodel.MainViewModel
import com.qltech.ui.BaseActivity
import com.qltech.ui.view.adapter.ITabPage
import com.qltech.ui.view.adapter.SimpleFragmentAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class MessageSaverActivity : BaseActivity(R.layout.activity_message_saver) {

    private lateinit var binding: ActivityMessageSaverBinding
    private val viewModel: IMainViewModel by viewModel<MainViewModel>()

    companion object {
        private val TAG = MessageSaverActivity::class.java.simpleName
    }

    private val fragmentAdapter: SimpleFragmentAdapter<HomePage> by lazy {
        SimpleFragmentAdapter(supportFragmentManager, listOf(HomePage.Status, HomePage.LocalStatus))
    }

    private var switchCount: Int = 0
    private var nativeAd: UnifiedNativeAd? = null
    private var interstitialAd: InterstitialAd = InterstitialAd(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initInterstitialAd()
        initBackNativeAd()

        permissionHelper.runOnPermissionGranted(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) {
            if (!it) {
                onPermissionsDenied()
            }
        }
    }

    private fun onPermissionsDenied() {
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.give_permission)
            .setMessage(R.string.need_permission_to_work)
            .setPositiveButton(R.string.yes) { _, _ ->
                IntentUtils.toAppSettings(this)
            }
            .setOnDismissListener {
                finish()
            }
            .show()
    }

    private fun initInterstitialAd() {
        interstitialAd.adUnitId = AdManager.getPageSwitchInterstitialId()
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        interstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun initData() {
        subscribe(viewModel.operateEnum) {
            when (it) {
                OperateEnum.NONE -> {
                    setActionBarTitle(R.string.status_saver)
                    setDisplayHomeAsUpEnabled(true)
                }
                else -> {
                    setActionBarTitle("1 selected")
                    setDisplayHomeAsUpEnabled(true)
                }
            }
        }
        subscribe(viewModel.selectedNum) {
            if (it > 0) {
                setActionBarTitle("$it selected")
            }
        }
    }

    private fun initBackNativeAd() {
        val adLoader = AdLoader.Builder(this, AdManager.getBackDialogNativeId())
            .forUnifiedNativeAd { unifiedNativeAd ->
                nativeAd = unifiedNativeAd
            }.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun initView() {

        binding = getBinding() as ActivityMessageSaverBinding
        initActionBar(binding.toolBar)
        setActionBarTitle(R.string.status_saver)
        setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        setDisplayHomeAsUpEnabled(true)
        initPager()

        initBannerAd()
    }

    private fun initPager() {

        binding.viewPager.overScrollMode = View.OVER_SCROLL_NEVER
        binding.viewPager.adapter = fragmentAdapter
        binding.viewPager.currentItem = 0
        setTabTitleStatus(binding.viewPager.currentItem)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                setTabTitleStatus(position)
                if (!interstitialAd.isLoaded) {
                    return
                }
                switchCount++
                val interval = AdRemoteConfig.getAdSwitchPageInterval()
                if (interval != 0 && switchCount % interval == 0) {
                    interstitialAd.show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) = Unit

        })
        binding.tabStatus.setOnClickListener {
            if (binding.viewPager.currentItem != 0) {
                binding.viewPager.currentItem = 0
            }
        }
        binding.tabDownload.setOnClickListener {
            if (binding.viewPager.currentItem != 1) {
                binding.viewPager.currentItem = 1
            }
        }
    }

    private fun setTabTitleStatus(position: Int) {
        when (position) {
            0 -> {
                binding.tabStatus.setTextColor(ContextCompat.getColor(this, R.color.tab_selected))
                binding.tabStatus.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                binding.tabDownload.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.tab_unselected
                    )
                )
                binding.tabDownload.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }
            1 -> {
                binding.tabStatus.setTextColor(ContextCompat.getColor(this, R.color.tab_unselected))
                binding.tabStatus.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                binding.tabDownload.setTextColor(ContextCompat.getColor(this, R.color.tab_selected))
                binding.tabDownload.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
        }
    }


    private fun initBannerAd() {
        if (AdRemoteConfig.getAdSwitch()) {
            val adView = AdView(this)
            adView.adSize = adSize
            adView.adUnitId = AdManager.getMessageSaverBannerId()

            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            binding.bannerAdLayout.addView(adView)
        }
    }

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.bannerAdLayout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onResume() {
        super.onResume()

    }

//    override fun onBackPressed() {
//        if (!onFragmentBackPressed()) {
//            showBackCheckDialog()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()

        nativeAd?.destroy()
        nativeAd = null
    }

    private fun showBackCheckDialog() {
        val alertDialog = AlertDialog.Builder(this)

        if (AdRemoteConfig.getAdSwitch()) {
            nativeAd?.let {
                val adView =
                    layoutInflater.inflate(R.layout.ad_unified_s, null) as UnifiedNativeAdView

                val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                headlineView.text = it.headline
                adView.headlineView = headlineView

                val bodyView = adView.findViewById<TextView>(R.id.ad_body)
                if (it.body == null) {
                    bodyView.visibility = View.INVISIBLE
                } else {
                    bodyView.visibility = View.VISIBLE
                    bodyView.text = it.body
                    adView.bodyView = bodyView
                }

                val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
                if (it.icon == null) {
                    iconView.visibility = View.GONE
                } else {
                    iconView.visibility = View.VISIBLE
                    iconView.setImageDrawable(it.icon.drawable)
                    adView.iconView = iconView
                }

                val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
                if (it.callToAction == null) {
                    callToActionView.visibility = View.INVISIBLE
                } else {
                    callToActionView.visibility = View.VISIBLE
                    callToActionView.text = it.callToAction
                    adView.callToActionView = callToActionView
                }

                val priceView = adView.findViewById<TextView>(R.id.ad_price)
                if (it.price == null) {
                    priceView.visibility = View.INVISIBLE
                } else {
                    priceView.visibility = View.VISIBLE
                    priceView.text = it.price
                    adView.priceView = priceView
                }

                val storeView = adView.findViewById<TextView>(R.id.ad_store)
                if (it.store == null) {
                    storeView.visibility = View.INVISIBLE
                } else {
                    storeView.visibility = View.VISIBLE
                    storeView.text = it.store
                    adView.storeView = storeView
                }

                val startView = adView.findViewById<RatingBar>(R.id.ad_stars)
                if (it.starRating == null) {
                    startView.visibility = View.INVISIBLE
                } else {
                    startView.visibility = View.VISIBLE
                    startView.rating = it.starRating.toFloat()
                    adView.starRatingView = startView
                }

                val advertiserView = adView.findViewById<TextView>(R.id.ad_advertiser)
                if (it.advertiser == null) {
                    advertiserView.visibility = View.INVISIBLE
                } else {
                    advertiserView.visibility = View.VISIBLE
                    advertiserView.text = it.advertiser
                    adView.advertiserView = advertiserView
                }

                val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
                mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                mediaView.setMediaContent(it.mediaContent)
                adView.mediaView = mediaView

                adView.setNativeAd(it)
                alertDialog.setView(adView)
            }
        }

        alertDialog.setTitle(R.string.exit)
            .setMessage(R.string.dialog_exit_app)

            .setPositiveButton(R.string.yes) { _, _ ->
                finish()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private sealed class HomePage : ITabPage {

        val stringProvider = AppHelper.stringHelper

        object Status : HomePage() {

            override val tabTitle: String =
                stringProvider.getString(R.string.tab_status)

            override fun getFragment(): Fragment {
                return MessageFragment().apply {
                    putArgs(MessageArguments(MessageSourceEnum.WHATS_APP))
                }
            }
        }

        object LocalStatus : HomePage() {
            override val tabTitle: String =
                stringProvider.getString(R.string.tab_downloaded)

            override fun getFragment(): Fragment {
                return MessageFragment().apply {
                    putArgs(MessageArguments(MessageSourceEnum.LOCAL))
                }
            }
        }
    }
}