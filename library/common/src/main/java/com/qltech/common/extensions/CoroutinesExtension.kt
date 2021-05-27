package com.qltech.common.extensions

import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

fun <T> CancellableContinuation<T>.resumeWhenActive(input: T) =
    this.takeIf { it.isActive }?.resume(input)