package com.virtual.box.core.server.am.entity

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service running token
 */
class RunningServiceRecord(
    val intent: Intent,
    val serviceInfo: ServiceInfo
) : Binder() {
    // onStartCommand startId
    val startId = AtomicInteger(1)
    val bindCount = AtomicInteger(0)

    val andIncrementStartId: Int
        get() = startId.getAndIncrement()

    fun decrementBindCountAndGet(): Int {
        return bindCount.decrementAndGet()
    }

    fun incrementBindCountAndGet(): Int {
        return bindCount.incrementAndGet()
    }
}