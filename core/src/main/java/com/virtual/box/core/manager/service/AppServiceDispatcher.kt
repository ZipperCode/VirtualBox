package com.virtual.box.core.manager.service

import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Intent
import android.content.Intent.FilterComparison
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.entity.AppServiceRecord
import com.virtual.box.core.entity.VmProxyServiceRecord
import com.virtual.box.core.manager.AppActivityThread

internal class AppServiceDispatcher {

    private val logger = Logger.getLogger(L.VM_TAG,"AppServiceDispatcher")

    private val serviceRecord: MutableMap<FilterComparison, AppServiceRecord> = HashMap()


}