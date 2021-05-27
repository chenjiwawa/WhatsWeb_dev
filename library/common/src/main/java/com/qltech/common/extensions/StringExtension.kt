package com.qltech.common.extensions

import android.text.Editable

fun String?.getWithoutSpace(): String? {
    return this?.replace(" ", "")
}

fun Editable?.getWithoutSpace(): String? {
    return this?.toString()?.replace(" ", "")
}

fun String.isNumericPositive(): Boolean {
    return try {
        this.toDouble() > 0
    } catch (e: NumberFormatException) {
        false
    }
}
