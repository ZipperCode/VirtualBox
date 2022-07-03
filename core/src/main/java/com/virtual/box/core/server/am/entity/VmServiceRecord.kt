package com.virtual.box.core.server.am.entity

import android.content.ComponentName
import android.content.Intent
import android.content.Intent.FilterComparison
import android.content.pm.ApplicationInfo
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.collection.ArrayMap
import com.virtual.box.core.entity.VmProcessRecord
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


/**
 *
 * @author  zhangzhipeng
 * @date    2022/6/29
 */
class VmServiceRecord(
    val userId: Int,
    val componentName: ComponentName,
    val intent: Intent,
    val serviceInfo: ServiceInfo
) : Binder(){

    var app: VmProcessRecord? = null

    val isStarted: AtomicBoolean = AtomicBoolean(false)

    var executingStart: Long = 0

    val filter = FilterComparison(intent)

    val startId = AtomicInteger(1)

    val bindCount = AtomicInteger(0)

    /**
     * 是否被关闭了
     */
    val hasDeath = AtomicBoolean(false)

    val connections = HashMap<IBinder, ArrayList<VmConnectionServiceRecord>>()

    fun unbindService(token: IBinder, connRecord: VmConnectionServiceRecord){
        if (connections.containsKey(token)){
            val connRecordList = connections[token] ?: return
            val iterator = connRecordList.iterator()
            while (iterator.hasNext()){
                val entry = iterator.next()
                if (entry == connRecord){
                    iterator.remove()
                    return
                }
            }
        }
    }

    fun serviceOnStart(){
        if (startId.getAndIncrement() == 0){
            isStarted.set(true)
        }
    }

    fun onServiceOnStop(){
        val id = startId.decrementAndGet()
        if (id <= 0){
            isStarted.set(false)
        }
    }

    fun onServiceBind(){
        if (bindCount.getAndIncrement() == 0){
            isStarted.set(true)
        }
    }

    fun onServiceUnBind(){
        val id = bindCount.decrementAndGet()
        if (id <= 0){
            isStarted.set(false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VmServiceRecord

        if (userId != other.userId) return false
        if (componentName != other.componentName) return false
        if (serviceInfo != other.serviceInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId
        result = 31 * result + componentName.hashCode()
        result = 31 * result + serviceInfo.hashCode()
        return result
    }


}