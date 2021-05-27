package com.qltech.messagesaver

import com.qltech.base.helper.AppHelper
import com.qltech.messagesaver.arguments.MessageArguments
import com.qltech.messagesaver.arguments.MessageDetailArguments
import com.qltech.firebase.helper.AnalysisHelper
import com.qltech.firebase.remoteconfig.AdRemoteConfig
import com.qltech.messagesaver.model.enums.MessageSourceEnum
import com.qltech.whatsweb.Repository.*
import com.qltech.messagesaver.usecase.IMessageUseCase
import com.qltech.messagesaver.usecase.LocalMessageUseCase
import com.qltech.messagesaver.usecase.MessageUseCase
import com.qltech.messagesaver.viewmodel.*
import com.qltech.ui.helper.IAnalysisHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mUtilsModules = module {
    single<IAnalysisHelper> { AnalysisHelper }
    single { AdRemoteConfig }
}

val mViewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { (arguments: MessageArguments) -> MessageViewModel(get(named(arguments.messageEnum))) }
    viewModel { (arguments: MessageDetailArguments) -> MessageDetailViewModel(arguments, get(named(arguments.messageEnum))) }
}

val mUseCaseModules = module {
    factory<IMessageUseCase>(named(MessageSourceEnum.WHATS_APP)) { MessageUseCase(get(), get(named(MessageSourceEnum.WHATS_APP)), get()) }
    factory<IMessageUseCase>(named(MessageSourceEnum.LOCAL)) { LocalMessageUseCase(get(), get(named(MessageSourceEnum.LOCAL)), get()) }
}

val mRepositoryModules = module {
    factory<IFileRepository> { FileRepository() }
    factory<IMessageRepository> { MessageRepository() }

    single<IMessageCacheRepository>(named(MessageSourceEnum.WHATS_APP)) { MessageCacheRepository() }
    single<IMessageCacheRepository>(named(MessageSourceEnum.LOCAL)) { MessageCacheRepository() }
}

val mApiModules = module {
}

val mAppProviderModules = module {
    single { AppHelper.stringHelper }
    single { AppHelper.dimensionHelper }
    single { AppHelper.drawableHelper }
    single { AppHelper.colorHelper }
    single { AppHelper.toastHelper }
    single { AppHelper.sharedPreferencesHelper }
    single { AppHelper.fileHelper }
}