package com.qltech.common.extensions

import java.math.BigDecimal

fun <T> MutableList<T>.addAllToLast(elements: List<T>): MutableList<T> {
    elements.forEach {
        if (!contains(it)) {
            add(it)
        }
    }
    return this
}

fun <T, R> List<T>.getSameValue(getValue: (T) -> R): R? {
    return map { getValue(it) }
        .toSet()
        .takeIf { 1 == it.size }
        ?.firstOrNull()
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum: BigDecimal = BigDecimal(0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}