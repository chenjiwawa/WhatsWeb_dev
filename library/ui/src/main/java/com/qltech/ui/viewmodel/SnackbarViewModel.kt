package com.qltech.ui.viewmodel

import androidx.annotation.StringRes
import com.qltech.common.helper.ISingleLiveObserver
import com.qltech.common.helper.OneShotLiveEvent
import com.qltech.base.helper.AppHelper
import com.qltech.base.helper.IString
import com.qltech.ui.model.SnackMessage

interface ISnackBarViewModel {
    val snackBarMessageLiveData: ISingleLiveObserver<SnackMessage>

    fun showSnackBarMessage(type: SnackMessage.Type, @StringRes messageRes: Int)
    fun showSnackBarMessage(type: SnackMessage.Type, message: String)
}

class SnackBarViewModel : ISnackBarViewModel {
    override val snackBarMessageLiveData: OneShotLiveEvent<SnackMessage> = OneShotLiveEvent()

    private val stringProvider: IString = AppHelper.stringHelper

    override fun showSnackBarMessage(type: SnackMessage.Type, messageRes: Int) {
        showSnackBarMessage(type, stringProvider.getString(messageRes))
    }

    override fun showSnackBarMessage(type: SnackMessage.Type, message: String) {
        snackBarMessageLiveData.value = SnackMessage(type, message)
    }
}
