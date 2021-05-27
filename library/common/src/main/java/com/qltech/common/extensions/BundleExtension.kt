package com.qltech.common.extensions

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun Bundle.getNonNullByteArray(key: String): ByteArray {
    return getByteArray(key)
        ?: throw IllegalArgumentException("There is no data in the Bundle that can be found by the $key.")
}

fun Bundle.getNonNullCharArray(key: String): CharArray {
    return getCharArray(key)
        ?: throw IllegalArgumentException("There is no data in the Bundle that can be found by the $key.")
}

fun Bundle.getNonNullString(key: String): String {
    return getString(key)
        ?: throw IllegalArgumentException("There is no data in the Bundle that can be found by the $key.")
}

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> Bundle.getNonNullSerializable(key: String): T {
    return getSerializable(key) as? T
        ?: throw IllegalArgumentException("There is no data in the Bundle that can be found by the $key.")
}

fun <T : Parcelable> Bundle.getNonNullParcelable(key: String): T {
    return getParcelable(key)
        ?: throw IllegalArgumentException("There is no data in the Bundle that can be found by the $key.")
}

fun Bundle.getMap(): Map<String, Any?> {
    return keySet().associateWith { get(it) }
}