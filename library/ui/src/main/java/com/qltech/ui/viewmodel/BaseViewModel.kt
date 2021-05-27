package com.qltech.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.cancel

abstract class BaseViewModel(
    snackBarViewModel: ISnackBarViewModel = SnackBarViewModel(),
    loadingViewModel: ILoadingViewModel = LoadingViewModel(snackBarViewModel)
) : ViewModel(), ILoadingViewModel by loadingViewModel, ISnackBarViewModel by snackBarViewModel {

    override fun onCleared() {
        super.onCleared()
        cancel()
    }

}