package com.qltech.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.qltech.common.helper.PermissionHelper
import com.qltech.common.utils.IntentUtils
import com.qltech.ui.helper.IAnalysisHelper
import org.koin.android.ext.android.inject


abstract class BaseActivity(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity(layoutResId),
    IActionBar {

    private val toolBar: Toolbar? by lazy { findViewById(R.id.tool_bar) }

    val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.fragment_container
        ).also {
            toolBar?.run {
                val appBarConfiguration = AppBarConfiguration(it.graph)
                setupWithNavController(it, appBarConfiguration)
            }
        }
    }

    fun getBinding(): ViewDataBinding {
        return DataBindingUtil.setContentView(this, layoutResId)
    }

    private val analysisHelper: IAnalysisHelper by inject()
    val permissionHelper: PermissionHelper = PermissionHelper(lifecycleScope)

    open val isTrackPageEnable: Boolean = true
    protected open val screenType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar(toolBar)
    }

    override fun initActionBar(toolBar: Toolbar?) {
        if (null == toolBar) return

        setSupportActionBar(toolBar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolBar.rootWindowInsets?.run { onApplyWindowInsets(toolBar, this) }
        }

        toolBar.setOnApplyWindowInsetsListener { view, insets ->
            onApplyWindowInsets(view, insets)
        }

        toolBar.run {
            setNavigationOnClickListener {
                onNavigationClick()
            }
        }
    }

    override fun setActionBarTitle(@StringRes titleRes: Int) {
        supportActionBar?.setTitle(titleRes)
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setDisplayHomeAsUpEnabled(enable: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enable);
        supportActionBar?.setHomeButtonEnabled(enable);
    }

    fun setHomeAsUpIndicator(resId: Int) {
        supportActionBar?.setHomeAsUpIndicator(resId)
    }

    private fun onApplyWindowInsets(view: View, insets: WindowInsets): WindowInsets {
        view.setPaddingRelative(0, insets.systemWindowInsetTop, 0, 0)
        return insets.replaceSystemWindowInsets(
            insets.systemWindowInsetLeft,
            0,
            insets.systemWindowInsetRight,
            insets.systemWindowInsetBottom
        )
    }

    override fun onResume() {
        super.onResume()
        onTrackPageStart()
    }

    override fun onPause() {
        super.onPause()
        onTrackPageEnd()
    }

    override fun onBackPressed() {
        if (!onFragmentBackPressed()) {
            super.onBackPressed()
        }
    }

    fun onFragmentBackPressed(): Boolean {
        val onBackPressedList = supportFragmentManager.fragments.filterIsInstance<IBackPressed>()
        return onBackPressedList.any { it.onBackPressed() }
    }

    override fun onDestroy() {
        toolBar?.setOnApplyWindowInsetsListener(null)
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        IntentUtils.onActivityFinish(this, intent)

        val parentIntent = parentActivityIntent
        if (isTaskRoot && null != parentIntent) {
            IntentUtils.startActivity(this, parentIntent)
        }
    }

    private fun onTrackPageStart() {
        if (isTrackPageEnable) {
            analysisHelper.onPageBegin(this, getScreenName())
        }
    }

    private fun onTrackPageEnd() {
        if (isTrackPageEnable) {
            analysisHelper.onPageEnd(this, getScreenName())
        }
    }

    private fun getScreenName(): String {
        val screenName = javaClass.simpleName
        return if (TextUtils.isEmpty(screenType)) {
            screenName
        } else {
            "$screenName-$screenType"
        }
    }

    open fun onNavigationClick() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentUtils.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * kotlin 函数跳转
     */
    @JvmOverloads
    inline fun <reified T : Activity> Activity.startActivity(bundle: Bundle? = null) {
        val intent = Intent(this, T::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    @JvmOverloads
    inline fun <reified T : Activity> Activity.startActivityForResult(
        requestCode: Int,
        bundle: Bundle? = null
    ) {
        val intent = Intent(this, T::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    init {
        //使用Svg
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}