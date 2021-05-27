package com.qltech.messagesaver.arguments

import android.os.Parcelable
import com.qltech.common.args.BundleArgs
import com.qltech.messagesaver.model.enums.MessageSourceEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageArguments(
    val messageEnum: MessageSourceEnum
) : BundleArgs, Parcelable
