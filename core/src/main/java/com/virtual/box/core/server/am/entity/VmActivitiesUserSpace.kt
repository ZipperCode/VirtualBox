package com.virtual.box.core.server.am.entity

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.manager.VmPackageManager
import com.virtual.box.core.server.pm.VmPackageManagerService

/**
 * 虚拟的用户活动空间
 * 管理用户空间下的所有组件的状态
 */
class VmActivitiesUserSpace(val userId: Int) {

    val logger = Logger.getLogger(L.SERVER_TAG, "VmActivitiesUserSpace")
    /**
     * Activity任务栈
     */
    val activityStack: VmActivityStack = VmActivityStack()
    /**
     * 启动Activity
     * @param userId    用户id
     * @param intent   intent
     * @param resolvedType 类型
     * @param resultTo todo 暂时未知
     * @param resultWho todo 暂时未知
     * @param requestCode 请求码
     * @param flags PackageManager.GET_ACTIVITY
     * @param options bundle
     * @return result
     */
    fun launchActivity(
        userId: Int, intent: Intent, resolvedType: String?, resultTo: IBinder?,
        resultWho: String?, requestCode: Int, flags: Int, options: Bundle?
    ){
        logger.method("userId = %s, intent = %s, resolvedType = %s, resultTo = %s, resultWho = %s, requestCode = %s, flags = %s, options = %s",
            userId, intent, resolvedType, resultTo, resultWho, requestCode, flags, options)


    }
}