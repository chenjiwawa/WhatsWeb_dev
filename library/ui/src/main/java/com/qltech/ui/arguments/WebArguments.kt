package com.qltech.ui.arguments

import android.os.Parcelable
import com.qltech.common.args.BundleArgs
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WebArguments(
    val url: String,
    val title: String? = null,
) : BundleArgs, Parcelable
