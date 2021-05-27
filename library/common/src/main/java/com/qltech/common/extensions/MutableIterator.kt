package com.qltech.common.extensions

fun <T> MutableIterator<T>.filter(operation: (data: T) -> Boolean): MutableIterator<T> {
    while (hasNext()) {
        val data = next()
        if (!operation(data)) {
            remove()
        }
    }
    return this
}