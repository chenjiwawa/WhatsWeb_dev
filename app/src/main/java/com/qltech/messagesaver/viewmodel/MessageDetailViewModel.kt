package com.qltech.messagesaver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qltech.whatsweb.R
import com.qltech.messagesaver.arguments.MessageDetailArguments
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.usecase.IMessageUseCase
import com.qltech.ui.model.SnackMessage
import com.qltech.ui.view.adapter.AdapterData
import com.qltech.ui.viewmodel.BaseViewModel
import com.qltech.ui.viewmodel.ILoadingViewModel
import com.qltech.ui.viewmodel.ISnackBarViewModel

interface IMessageDetailViewModel : ILoadingViewModel, ISnackBarViewModel {
    val currentMessage: LiveData<Message>
    val messageList: LiveData<List<AdapterData.Data<HomeEnum>>>

    fun refresh()
    fun onMessageSelected(message: Message)
    fun downloadMessage(message: Message)
    fun removeMessage(message: Message)
}

class MessageDetailViewModel(
    arguments: MessageDetailArguments,
    private val messageUseCase: IMessageUseCase
) : BaseViewModel(), IMessageDetailViewModel {

    companion object {
        private val TAG = MessageDetailViewModel::class.java.simpleName
    }

    override val currentMessage: MutableLiveData<Message> = MutableLiveData(arguments.currentMessage)
    override val messageList: MutableLiveData<List<AdapterData.Data<HomeEnum>>> = MutableLiveData()

    init {
        refresh()
    }

    override fun refresh() {
        runOnLoading {
            messageList.value = messageUseCase.getCacheMessage()
        }
    }

    override fun onMessageSelected(message: Message) {
        currentMessage.value = message
    }

    override fun downloadMessage(message: Message) {
        runOnLoading {
            val success = messageUseCase.downloadMessage(message)
            if (success) {
                showSnackBarMessage(SnackMessage.Type.NORMAL, R.string.msg_download_success)
            } else {
                showSnackBarMessage(SnackMessage.Type.ERROR,R.string.msg_download_fail )
            }
        }
    }

    override fun removeMessage(message: Message) {
        runOnLoading {
            val removeSuccess = messageUseCase.removeMessage(message)
            if (removeSuccess) {
                val index = messageList.value?.indexOf(message)
                if (null != index && index >= 0) {
                    messageList.value = messageList.value?.toMutableList()?.apply {
                        removeAt(index)
                    }
                }
            }
        }
    }
}