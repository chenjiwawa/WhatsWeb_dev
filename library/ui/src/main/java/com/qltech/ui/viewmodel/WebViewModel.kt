package com.qltech.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qltech.ui.arguments.WebArguments

interface IWebViewModel {
    val titleLiveData: LiveData<String>
    val urlLiveData: LiveData<String>
}

class WebViewModel(
    arguments: WebArguments
) : BaseViewModel(), IWebViewModel {

    override val titleLiveData: MutableLiveData<String> = MutableLiveData()
    override val urlLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        titleLiveData.value = arguments.title ?: ""
        arguments.url.takeIf { it.isNotBlank() }?.run {
            urlLiveData.value = this
        }
    }

}