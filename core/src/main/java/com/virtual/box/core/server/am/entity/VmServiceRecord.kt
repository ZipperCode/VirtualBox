package com.virtual.box.core.server.am.entity

import android.content.Intent
import android.content.Intent.FilterComparison
import android.content.pm.ApplicationInfo
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.collection.ArrayMap
import java.util.concurrent.atomic.AtomicInteger


/**
 *
 * @author  zhangzhipeng
 * @date    2022/6/29
 */
class VmServiceRecord(
    val userId: String,
    intent: Intent,
    val serviceInfo: ServiceInfo
) : Binder(){

    val intent = FilterComparison(intent)

    val startId = AtomicInteger(1)
    val bindCount = AtomicInteger(0)

}