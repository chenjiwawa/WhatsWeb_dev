package com.qltech.messagesaver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.qltech.common.helper.ISingleLiveObserver
import com.qltech.common.helper.SingleLiveEvent
import com.qltech.whatsweb.R
import com.qltech.messagesaver.common.utils.AdUtils
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.messagesaver.model.Selectable
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.messagesaver.usecase.IMessageUseCase
import com.qltech.ui.model.SnackMessage
import com.qltech.ui.view.adapter.AdapterData
import com.qltech.ui.viewmodel.BaseViewModel
import com.qltech.ui.viewmodel.ILoadingViewModel
import com.qltech.ui.viewmodel.ISnackBarViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

interface IMessageViewModel : ILoadingViewModel, ISnackBarViewModel {
    val operateEnum: LiveData<OperateEnum>
    val messageList: LiveData<List<AdapterData.Data<HomeEnum>>>
    val toMessageDetailPage: ISingleLiveObserver<Message>
    val selectedNum: LiveData<Int>

    fun setOperateMode(anEnum: OperateEnum)
    fun refresh()
    fun monitorMessageFileChange()
    fun onMessageClick(message: Message)
    fun downloadMessage(message: Message)
    fun onDeleteClick()
    fun setAdMobEnable(adSwitch: Boolean)
    fun downloadMessageList()
    fun setSelectedNum(num: Int)
}

class MessageViewModel(
    private val messageUseCase: IMessageUseCase
) : BaseViewModel(), IMessageViewModel {

    companion object {
        private val TAG = MessageViewModel::class.java.simpleName
    }

    override val operateEnum: MutableLiveData<OperateEnum> = MutableLiveData(
        OperateEnum.NONE
    )

    override val selectedNum: MutableLiveData<Int> = MutableLiveData(0)

    override val messageList: MutableLiveData<List<AdapterData.Data<HomeEnum>>> = MutableLiveData()

    override val toMessageDetailPage: SingleLiveEvent<Message> = SingleLiveEvent()

    private var monitorJob: Job? = null

    private var hasAdMob: Boolean = false

    override fun setOperateMode(anEnum: OperateEnum) {
        if (operateEnum.value == anEnum) return

        val clearSelected = OperateEnum.NONE == anEnum
        operateEnum.value = anEnum
        messageList.value = messageList.value?.apply {
            filterIsInstance<Message>().filterNot { it.isAdMob }.forEach {
                it.anEnum = anEnum
                if (clearSelected) {
                    it.isSelected = false
                }
            }
        }
    }

    override fun setAdMobEnable(adSwitch: Boolean) {
        hasAdMob = adSwitch && AdRemoteConfig.getAdStatusGridIntervalIndex() > 0
    }

    override fun refresh() {
        runOnLoading {
            val list = messageUseCase.getMessage().toMutableList()
            if (hasAdMob && list.size >= AdRemoteConfig.getAdStatusGridSizeMinLimit() && list.isNotEmpty()) {
                val adListIndex = AdUtils.calculateAdPosition(
                    list,
                    true,
                    AdRemoteConfig.getAdStatusGridStartIndex(),
                    AdRemoteConfig.getAdStatusGridIntervalIndex()
                )
                adListIndex?.forEach {
                    list.add(it, Message(isAdMob = true))
                }
            }
            messageList.value = list
        }
    }

    override fun monitorMessageFileChange() {
        if (true == monitorJob?.isActive) {
            refresh()
            return
        }

        monitorJob = viewModelScope.launch {
            messageUseCase.getMessageFlow().collect {
                val list = it.toMutableList()
                if (hasAdMob && list.size >= AdRemoteConfig.getAdStatusGridSizeMinLimit() && list.isNotEmpty()) {
                    val adListIndex = AdUtils.calculateAdPosition(
                        list,
                        true,
                        AdRemoteConfig.getAdStatusGridStartIndex(),
                        AdRemoteConfig.getAdStatusGridIntervalIndex()
                    )
                    adListIndex?.forEach { index ->
                        list.add(index, Message(isAdMob = true))
                    }
                }
                messageList.value = list
            }
        }
    }

    override fun onMessageClick(message: Message) {
        when (operateEnum.value) {
            OperateEnum.EDIT -> {
                message.isSelected = !message.isSelected
                messageList.value = messageList.value
                if (true != messageList.value?.filterIsInstance<Selectable>()
                        ?.any { it.isSelected }
                ) {
                    setOperateMode(OperateEnum.NONE)
                }
            }
            OperateEnum.MULTI_DOWNLOAD -> {
                message.isSelected = !message.isSelected
                messageList.value = messageList.value
                if (true != messageList.value?.filterIsInstance<Selectable>()?.any {
                        it.isSelected
                    }) {
                    setOperateMode(OperateEnum.NONE)
                }
            }
            else -> toMessageDetailPage.value = message
        }
        if (operateEnum.value != OperateEnum.NONE){
            val num = messageList.value?.filterIsInstance<Message>()
                ?.filter { it.isSelected }?.size ?: 0
            setSelectedNum(num)
        }
    }

    override fun downloadMessage(message: Message) {
        runOnLoading {
            val success = messageUseCase.downloadMessage(message)
            if (success) {
                showSnackBarMessage(SnackMessage.Type.NORMAL, R.string.msg_download_success)
            } else {
                showSnackBarMessage(SnackMessage.Type.ERROR, R.string.msg_download_fail)
            }
        }
    }

    override fun downloadMessageList() {
        messageList.value?.apply {
            filterIsInstance<Message>().filter { it.isSelected }.run {
                runOnLoading {
                    val list = ArrayList<Message>()
                    list.clear()
                    this.forEach { message ->
                        val success = messageUseCase.downloadMessage(message)
                        if (success) {
                            list.add(message)
                        }
                    }
                    if (list.isNotEmpty()) {
                        showSnackBarMessage(
                            SnackMessage.Type.NORMAL,
                            R.string.msg_download_success
                        )
                    } else {
                        showSnackBarMessage(SnackMessage.Type.ERROR, R.string.msg_download_fail)
                    }
                }
            }
        }
    }

    override fun onDeleteClick() {
        runOnLoading {
            messageList.value?.filterIsInstance<Message>()
                ?.filter {
                    it.isSelected
                }
                ?.run {
                    messageUseCase.removeMessage(this)
                }
            setOperateMode(OperateEnum.NONE)
        }
    }

    override fun setSelectedNum(num: Int) {
        if (selectedNum.value == num) return
        selectedNum.value = num
    }
}