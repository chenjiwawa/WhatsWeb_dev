package com.qltech.messagesaver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qltech.whatsweb.R
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.ui.model.SnackMessage
import com.qltech.ui.viewmodel.BaseViewModel
import com.qltech.ui.viewmodel.ILoadingViewModel
import com.qltech.ui.viewmodel.ISnackBarViewModel

interface IMainViewModel : ILoadingViewModel, ISnackBarViewModel {
    val storagePermissionGranted: LiveData<Boolean>
    val operateEnum: LiveData<OperateEnum>
    val selectedNum: LiveData<Int>

    fun setStoragePermission(isPermissionGranted: Boolean)
    fun onReceivePermissionResult(isPermissionGranted: Boolean)
    fun setOperateMode(anEnum: OperateEnum)
    fun setSelectedNum(num: Int)
}

class MainViewModel : BaseViewModel(), IMainViewModel {

    override val storagePermissionGranted: MutableLiveData<Boolean> = MutableLiveData()
    override val operateEnum: MutableLiveData<OperateEnum> = MutableLiveData(
        OperateEnum.NONE
    )

    override val selectedNum: MutableLiveData<Int> = MutableLiveData(0)

    override fun setStoragePermission(isPermissionGranted: Boolean) {
        if (storagePermissionGranted.value == isPermissionGranted) return

        storagePermissionGranted.value = isPermissionGranted
    }

    override fun onReceivePermissionResult(isPermissionGranted: Boolean) {
        storagePermissionGranted.value = isPermissionGranted
        if (!isPermissionGranted) {
            showSnackBarMessage(SnackMessage.Type.ERROR, R.string.need_permission_to_work)
        }
    }

    override fun setOperateMode(anEnum: OperateEnum) {
        if (operateEnum.value == anEnum) return
        operateEnum.value = anEnum
    }

    override fun setSelectedNum(num: Int) {
        if (selectedNum.value == num) return
        selectedNum.value = num
    }
}