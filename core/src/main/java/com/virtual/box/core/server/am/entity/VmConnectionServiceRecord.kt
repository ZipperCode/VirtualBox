package com.virtual.box.core.server.am.entity

import android.app.IServiceConnection
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import java.util.concurrent.atomic.AtomicBoolean

class VmConnectionServiceRecord(
    val serviceRecord: VmServiceRecord,
    val token: IBinder,
    val intent: Intent,
    val callingPackageName: String,
    val callingProcessName: String,
    val conn: IServiceConnection,
    val serviceInfo: ServiceInfo
): IBinder.DeathRecipient{

    private var outerBinderDeathRecipient: IBinder.DeathRecipient? = null

    val hasBind = AtomicBoolean(false)

    val hasDeath = AtomicBoolean(false)

    fun setAndGetOuterBinderDeathRecipient(binderDeathRecipient: IBinder.DeathRecipient): IBinder.DeathRecipient{
        this.outerBinderDeathRecipient = binderDeathRecipient
        return this
    }

    fun getComponentName(): ComponentName{
        val componentName = intent.component
        if (componentName != null){
            return componentName
        }
        val packageName = intent.getPackage() ?: serviceInfo.packageName
        val claName = serviceInfo.name

        return ComponentName(packageName, claName)
    }

    override fun binderDied() {
        outerBinderDeathRecipient?.binderDied()
    }
}