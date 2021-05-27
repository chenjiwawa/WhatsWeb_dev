package com.qltech.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.qltech.common.helper.PermissionHelper
import com.qltech.common.utils.XLog
import com.qltech.ui.helper.IAnalysisHelper
import com.qltech.ui.view.XToolbar
import org.koin.android.ext.android.inject
import kotlin.system.measureTimeMillis

abstract class BaseFragment(@LayoutRes layoutResId: Int) : Fragment(layoutResId), IBackPressed {

    companion object {
        val TAG: String = BaseFragment::class.java.simpleName
    }

    val navController: NavController? by lazy {
        try {
            Navigation.findNavController(
                requireActivity(),
                R.id.fragment_container
            )
        } catch (e: IllegalStateException) {
            null
        }
    }

    private val analysisHelper: IAnalysisHelper by inject()
    val permissionHelper: PermissionHelper = PermissionHelper(lifecycleScope)

    protected open val isTrackPageEnable: Boolean = false
    protected open val screenType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View?
        val cost = measureTimeMillis {
            view = super.onCreateView(inflater, container, savedInstanceState)
        }
        XLog.d(TAG, "[onCreateView] cost time $cost when inflate $view")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActionBar()
    }

    private fun initActionBar() {
        val activity = activity as? BaseActivity ?: return

        val toolbar: XToolbar? = view?.findViewById(R.id.tool_bar)
        if (null != toolbar) {
            (activity as? IActionBar)?.initActionBar(toolbar)
        }
    }

    fun setActionBarTitle(@StringRes titleRes: Int) {
        (activity as? IActionBar)?.setActionBarTitle(titleRes)
    }

    fun setActionBarTitle(title: String) {
        (activity as? IActionBar)?.setActionBarTitle(title)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        //TalkingData
        if (isVisibleToUser) {
            onTrackPageStart()
        } else {
            onTrackPageEnd()
        }
    }

    override fun onResume() {
        super.onResume()
        onTrackPageStart()
    }

    override fun onPause() {
        super.onPause()
        onTrackPageEnd()
    }

    override fun onBackPressed(): Boolean = false

    private fun onTrackPageStart() {
        if (!isResumed || !userVisibleHint) return
        val context = context ?: return

        if (isTrackPageEnable) {
            analysisHelper.onPageBegin(context, getScreenName())
        }
    }

    private fun onTrackPageEnd() {
        if (!isResumed && !userVisibleHint) return
        val context = context ?: return

        if (isTrackPageEnable) {
            analysisHelper.onPageEnd(context, getScreenName())
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * 含有Bundle通过Class跳转界面 *
     */
    /**
     * kotlin 函数跳转
     */
    @JvmOverloads
    inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle? = null) {
        val intent = Intent(this.activity, T::class.java)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
}