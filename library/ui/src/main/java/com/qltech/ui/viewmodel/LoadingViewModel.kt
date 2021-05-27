package com.qltech.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParseException
import com.qltech.common.utils.XLog
import com.qltech.ui.exception.NoDataException
import com.qltech.ui.model.SnackMessage
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

interface ILoadingViewModel : CoroutineScope {
    val loadingLiveData: LiveData<Boolean>

    /**
     * @return false means there are other tasks running, this call failed
     */
    fun runOnLoading(showProgress: Boolean = true, action: suspend () -> Unit): Boolean
}

class LoadingViewModel(
    snackBarViewModel: ISnackBarViewModel,
    private val exceptionHandler: CoroutineExceptionHandler = LoadingExceptionHandler(snackBarViewModel)
) : ILoadingViewModel, CoroutineScope by (MainScope() + exceptionHandler) {

    override val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    override fun runOnLoading(showProgress: Boolean, action: suspend () -> Unit): Boolean {
        return if (true == loadingLiveData.value) {
            false
        } else {
            launch {
                try {
                    loadingLiveData.takeIf { showProgress }?.value = true
                    action()
                } finally {
                    loadingLiveData.value = false
                }
            }
            true
        }
    }

    private class LoadingExceptionHandler(
        private val snackBarViewModel: ISnackBarViewModel
    ) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {

        companion object {
            private val TAG = LoadingExceptionHandler::class.java.simpleName
        }

        override fun handleException(context: CoroutineContext, exception: Throwable) {
            when (exception) {
                is IOException,
                is JsonParseException -> {
                    XLog.e(TAG, "[LoadingViewModel] handleException", exception)
                    snackBarViewModel.showSnackBarMessage(SnackMessage.Type.ERROR, exception.message.toString())
                }
                is NoDataException -> {
                    snackBarViewModel.showSnackBarMessage(SnackMessage.Type.NORMAL, exception.message.toString())
                }
                else -> throw exception
            }
        }
    }
}