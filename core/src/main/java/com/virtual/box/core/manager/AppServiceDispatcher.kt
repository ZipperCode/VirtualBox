package com.virtual.box.core.manager

import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Intent
import android.content.Intent.FilterComparison
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.entity.AppServiceRecord
import com.virtual.box.core.entity.VmProxyServiceRecord

internal class AppServiceDispatcher {

    private val logger = Logger.getLogger(L.VM_TAG,"AppServiceDispatcher")

    private val serviceRecord: MutableMap<FilterComparison, AppServiceRecord> = HashMap()

    fun onStartCommand(shadowIntent: Intent, flags: Int, startId: Int): Int{
        val parseStubInfo = try {
            VmProxyServiceRecord.parseStubInfo(shadowIntent)
        }catch (e: Exception){
            logger.e(e)
            return START_NOT_STICKY
        }
        return START_NOT_STICKY
    }


    private fun getOrCreateService(stubServiceRecord: VmProxyServiceRecord): Service?{
        if (stubServiceRecord.serviceInfo == null){
            return null
        }
        val findServiceRecord = findServiceRecord(stubServiceRecord.originIntent)
        if (findServiceRecord?.service != null){
            return findServiceRecord.service
        }
        val service = VmAppActivityThread.handleCreateVmService(stubServiceRecord.serviceInfo, stubServiceRecord.token) ?: return null
        serviceRecord[FilterComparison(stubServiceRecord.originIntent)] = AppServiceRecord(service)
        return service
    }

    fun findServiceRecord(intent: Intent?): AppServiceRecord?{
        intent ?: return null
        return serviceRecord[FilterComparison(intent)]
    }
}