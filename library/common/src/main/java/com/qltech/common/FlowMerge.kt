package com.qltech.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

public fun <K, T> Iterable<K>.mergeListFlow(toFlow: (key: K) -> Flow<List<T>>): Flow<List<T>> {
    val tmpMap: MutableMap<K, List<T>> = HashMap()

    return map { key ->
        toFlow(key).map { value ->
            key to value
        }
    }.merge().map {
        tmpMap[it.first] = it.second
        tmpMap.values.flatten()
    }.distinctUntilChanged()
}