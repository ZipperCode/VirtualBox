package com.virtual.box.core.helper

import android.content.Intent
import android.content.pm.ActivityInfo
import com.virtual.box.core.entity.VmProxyActivityRecord
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.proxy.ProxyManifest

object IntentHelper {
    /**
     * 真实的
     */
    const val VM_REAL_INTENT_DATA = "VM_REAL_INTENT_DATA"

    /**
     * 跨进程启动Act用户Id
     */
    const val IPC_USER_ID_INTENT_KEY = "_VM_|_user_id_"

    const val IPC_ACTIVITY_INFO_INTENT_KEY = "_VM_|_activity_info_"

    const val IPC_ORIGIN_INTENT_INTENT_KEY = "_VM_|_origin_intent_"

    @JvmStatic
    fun containsFlag(intent: Intent, flag: Int): Boolean {
        return intent.flags and flag != 0
    }

    fun replaceActivityIntentInfo(intent: Intent){
        val originComponent = intent.component
        VmProcessManager.findAvailableVmPid()
    }

    /**
     * 保存源Intent和需要代理的ActivityInfo
     */
    fun saveStubInfo(shadow: Intent, originIntent: Intent, activityInfo: ActivityInfo, userId: Int){
        shadow.putExtra(IPC_USER_ID_INTENT_KEY, userId)
        shadow.putExtra(IPC_ORIGIN_INTENT_INTENT_KEY, originIntent)
        shadow.putExtra(IPC_ACTIVITY_INFO_INTENT_KEY, activityInfo)
    }

    /**
     * 解析占位的Intent，获取原来的activityInfo
     */
    fun parseIntent(shadowIntent: Intent): VmProxyActivityRecord {
        val userId: Int = shadowIntent.getIntExtra(IPC_USER_ID_INTENT_KEY, 0)
        val activityInfo: ActivityInfo = shadowIntent.getParcelableExtra(IPC_ACTIVITY_INFO_INTENT_KEY)!!
        val originIntent: Intent = shadowIntent.getParcelableExtra<Intent>(IPC_ORIGIN_INTENT_INTENT_KEY)!!
        return VmProxyActivityRecord(userId, originIntent, activityInfo)
    }
}