package com.qltech.messagesaver.arguments

import android.os.Parcelable
import com.qltech.common.args.BundleArgs
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.MessageSourceEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageDetailArguments(
    val messageEnum: MessageSourceEnum = MessageSourceEnum.LOCAL,
    val currentMessage: Message? = null
) : BundleArgs, Parcelable
