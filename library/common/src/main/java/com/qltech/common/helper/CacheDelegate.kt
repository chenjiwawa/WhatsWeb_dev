package com.qltech.common.helper

import com.qltech.common.repository.LocalRepository
import com.qltech.common.repository.LocalRepositoryImpl
import kotlin.reflect.KProperty

class CacheDelegate<T>(
    dataClass: Class<T>,
    private val cacheRepository: LocalRepositoryImpl<T> = LocalRepository(dataClass)
) {
    private var bean: T? = null

    operator fun getValue(source: Any, property: KProperty<*>): T {
        return bean.run {
            this ?: cacheRepository.getCacheData().also {
                bean = it
            }
        }
    }

    operator fun setValue(
        source: Any,
        property: KProperty<*>,
        bean: T
    ) {
        cacheRepository.setCacheData(bean)
        this.bean = bean
    }
}