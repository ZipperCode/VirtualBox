package com.virtual.box.core.helper

import android.content.Intent
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest

object IntentHelper {
    /**
     * 真实的
     */
    const val VM_REAL_INTENT_DATA = "VM_REAL_INTENT_DATA"

    fun replaceActivityIntentInfo(intent: Intent){
        val originComponent = intent.component
        VmProcessManager.findAvailableVmPid()
    }


}