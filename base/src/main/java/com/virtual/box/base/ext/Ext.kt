package com.virtual.box.base.ext

import java.util.concurrent.Callable
import kotlin.coroutines.suspendCoroutine


suspend fun<T> catchException(block: suspend () -> T?): T?{
    return try {
        block.invoke()
    }catch (e: Throwable){
        e.printStackTrace()
        null
    }
}

suspend fun<T> Callable<T>.suspendWait(): T{
    return suspendCoroutine {

    }
}
