package com.qltech

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.orhanobut.logger.Logger
import com.qltech.firebase.ad.AdManager
import com.qltech.messagesaver.common.utils.DensityUtils
import com.qltech.messagesaver.ui.messagesaver.MessageSaverActivity
import com.qltech.whatsweb.R
import com.qltech.whatsweb.ui.WhatsWebActivity
import com.qltech.whatsweb.ui.base.BaseActivity
import com.qltech.whatsweb.ui.event.LanguageEvent
import com.qltech.whatsweb.ui.setting.SettingActivity
import com.qltech.whatsweb.util.FirebaseAnalyticsRecoder
import com.qltech.whatsweb.util.LanguageUtil
import com.qltech.whatsweb.util.RemoteConfigConstants
import com.rockey.status.utils.update.InAppUpdateManager
import com.sxu.shadowdrawable.ShadowDrawable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.topbar_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MainActivity : BaseActivity() {

    private val REQUEST_CODE_UPDATE: Int = 1000
    private var nativeAd: UnifiedNativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ShadowDrawable.setShadowDrawable(
            whatsweb,
            Color.parseColor("#25D366"),
            DensityUtils.dp2px(baseContext, 4.0f),
            Color.parseColor("#cc25D366"),
            DensityUtils.dp2px(baseContext, 4.0f),
            DensityUtils.dp2px(baseContext, 2.0f),
            DensityUtils.dp2px(baseContext, 2.0f)
        );
        ShadowDrawable.setShadowDrawable(
            statussaver,
            Color.parseColor("#25D366"),
            DensityUtils.dp2px(baseContext, 4.0f),
            Color.parseColor("#cc25D366"),
            DensityUtils.dp2px(baseContext, 4.0f),
            DensityUtils.dp2px(baseContext, 2.0f),
            DensityUtils.dp2px(baseContext, 2.0f)
        );

        setting.setOnClickListener {
            FirebaseAnalyticsRecoder.getInstance(baseContext)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_SETTING)
            startActivity(Intent(baseContext, SettingActivity::class.java))
        }

        whatsweb.setOnClickListener {
            FirebaseAnalyticsRecoder.getInstance(baseContext)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_WHATSWEB)
            startActivity(Intent(baseContext, WhatsWebActivity::class.java))
        }

        statussaver.setOnClickListener {
            FirebaseAnalyticsRecoder.getInstance(baseContext)
                .record(FirebaseAnalyticsRecoder.FirebaseAnalyticsKey.SHOW_STATUSSAVER)
            startActivity(Intent(baseContext, MessageSaverActivity::class.java))
        }

        about.setText(
            String.format(
                getString(R.string.home_about),
                getString(R.string.versionName)
            )
        )

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            initBackNativeAd()
        }

        InAppUpdateManager.checkForUpdate(this, REQUEST_CODE_UPDATE)
    }

    private fun initBackNativeAd() {
        if (baseContext == null)
            return

        if (!(Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_MASTER_SWITCH].asBoolean() && Firebase.remoteConfig[RemoteConfigConstants.AdsKey.AD_NATIVE_BACK_DIALOG].asBoolean()))
            return

        val adLoader = AdLoader.Builder(this, AdManager.getBackDialogNativeId())
            .forUnifiedNativeAd { unifiedNativeAd ->
                nativeAd = unifiedNativeAd
            }.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        FirebaseAnalyticsRecoder.getInstance(baseContext)
            .recordScreen(
                WhatsWebActivity::class.java.simpleName,
                FirebaseAnalyticsRecoder.Page.MAIN
            )
    }

    override fun onDestroy() {
        super.onDestroy()

        nativeAd?.destroy()
        nativeAd = null
    }

    override fun onBackPressed() {
        showBackCheckDialog()
    }

    private fun showBackCheckDialog() {
        val alertDialog = AlertDialog.Builder(this)

        nativeAd?.let {
            val adView = layoutInflater.inflate(R.layout.ad_unified, null) as UnifiedNativeAdView

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

        alertDialog.setTitle(R.string.exit)
            .setMessage(R.string.dialog_exit_app)

            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun isOpenEventbus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LanguageEvent?) {
        Logger.d(" onMessageEvent ChangeLanguageEvent " + event?.language)

        LanguageUtil.updateLanguageConfiguration(
            applicationContext,
            Locale(event?.language?.languageSimplified, event?.language?.countrySimplified)
        )
        restart()
    }

    fun restart() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.e("onActivityResult requestCode:$requestCode resultCode:$resultCode")
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != RESULT_OK) {

                // If the update is cancelled by the user,
                // you can request to start the update again.
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}