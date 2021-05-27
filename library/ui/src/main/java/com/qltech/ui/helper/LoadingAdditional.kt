package com.qltech.ui.helper

import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qltech.common.extensions.subscribe
import com.qltech.common.extensions.visible
import com.qltech.ui.R

interface LoadingAdditional {

    fun AppCompatActivity.bindLoading(loadingLiveData: LiveData<Boolean>) {
        subscribe(loadingLiveData) {
            applyLoading(it, findViewById(R.id.container))
        }
    }

    fun Fragment.bindLoading(loadingLiveData: LiveData<Boolean>) {
        subscribe(loadingLiveData) {
            applyLoading(it, view)
        }
    }

    fun AppCompatActivity.bindLoading(loadingLiveData: LiveData<Boolean>, container: View) {
        subscribe(loadingLiveData) {
            applyLoading(it, container)
        }
    }

    fun Fragment.bindLoading(loadingLiveData: LiveData<Boolean>, container: View) {
        subscribe(loadingLiveData) {
            applyLoading(it, container)
        }
    }

    fun AppCompatActivity.bindLoading(
        loadingLiveData: LiveData<Boolean>,
        progressBar: ProgressBar,
        swipeRefreshLayout: SwipeRefreshLayout? = null
    ) {
        subscribe(loadingLiveData) {
            applyLoading(it, findViewById(R.id.container), progressBar, swipeRefreshLayout)
        }
    }

    fun Fragment.bindLoading(
        loadingLiveData: LiveData<Boolean>,
        progressBar: ProgressBar,
        swipeRefreshLayout: SwipeRefreshLayout? = null
    ) {
        subscribe(loadingLiveData) {
            applyLoading(it, view, progressBar, swipeRefreshLayout)
        }
    }

    private fun applyLoading(
        isLoading: Boolean,
        container: View?,
        progressBar: ProgressBar? = container?.findViewById(R.id.progress_bar) as? ProgressBar,
        swipeRefreshLayout: SwipeRefreshLayout? = container?.findViewById(R.id.swipe_refresh_layout) as? SwipeRefreshLayout,
    ) {
        if (true == swipeRefreshLayout?.isRefreshing) {
            swipeRefreshLayout.isRefreshing = isLoading
        } else {
            progressBar?.visible = isLoading
        }
    }
}