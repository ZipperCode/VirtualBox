package com.virtual.box.core.hook.service

import android.app.usage.ExternalStorageStats
import android.app.usage.StorageStats
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ParceledListSlice
import android.os.Build
import android.os.IInterface
import android.os.Process
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger.Companion.getLogger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.MethodHandle
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.usage.HIStorageStatsManager
import java.io.IOException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*


@RequiresApi(api = Build.VERSION_CODES.O)
class IStorageStatsManagerHookHandle : BaseBinderHookHandle(Context.STORAGE_STATS_SERVICE) {

    private val logger = getLogger(L.HOOK_TAG, "IStorageStatsManagerHookHandle")

    override fun getOriginObject(): Any? {
        val originObj = HIStorageStatsManager.Stub.asInterface.call(originBinder)
        logger.i("getOriginObject#isProxy = %s", originObj is Proxy)
        return originObj
    }

    override fun hookInject(target: Any, proxy: Any) {
        logger.i("hookInject > target = %s, proxy = %s", target, proxy)
        logger.i("hookInject#target isProxy = %s", target is Proxy)
        logger.i("hookInject#proxy isProxy = %s", proxy is Proxy)
        super.hookInject(target, proxy)
        val systemService: StorageStatsManager = VirtualBox.get().hostContext.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        MirrorReflection.on("android.app.usage.StorageStatsManager")
            .field<Any>("mService")[systemService] = proxy
        var uuid: UUID?

        for (item in (VirtualBox.get().hostContext.getSystemService("storage") as StorageManager).getStorageVolumes()) {
            val uuidStr = item.uuid
            var storageStats: StorageStats? = null
            try {
                storageStats = systemService.queryStatsForUid(StorageManager.UUID_DEFAULT, getUid())
                storageStats.cacheBytes + storageStats.dataBytes + storageStats.appBytes
            } catch (e: IOException) {
                logger.e(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
                L.printStackTrace(e)
            }

        }

    }

    fun getUid(): Int {
        return try {
            VirtualBox.get().hostContext
                .packageManager.getApplicationInfo(VirtualBox.get().hostPkg, 128).uid
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }

    fun isQuotaSupported(methodHandle: MethodHandle, volumeUuid: String?, callingPackage: String?): Boolean {
        logger.i("isQuotaSupported > volumeUuid = %s, callingPackage = %s", volumeUuid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg)) as Boolean
    }

    fun isReservedSupported(methodHandle: MethodHandle, volumeUuid: String?, callingPackage: String?): Boolean {
        logger.i("isReservedSupported > volumeUuid = %s, callingPackage = %s", volumeUuid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg)) as Boolean
    }

    fun getTotalBytes(methodHandle: MethodHandle, volumeUuid: String?, callingPackage: String?): Long {
        logger.i("getTotalBytes > volumeUuid = %s, callingPackage = %s", volumeUuid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg)) as Long
    }

    fun getFreeBytes(methodHandle: MethodHandle, volumeUuid: String?, callingPackage: String?): Long {
        logger.i("getFreeBytes > volumeUuid = %s, callingPackage = %s", volumeUuid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg)) as Long
    }

    fun getCacheBytes(methodHandle: MethodHandle, volumeUuid: String?, callingPackage: String?): Long {
        logger.i("getCacheBytes > volumeUuid = %s, callingPackage = %s", volumeUuid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg)) as Long
    }

    fun getCacheQuotaBytes(methodHandle: MethodHandle, volumeUuid: String?, uid: Int, callingPackage: String?): Long {
        logger.i("getCacheQuotaBytes > volumeUuid = %s, uid = %s, callingPackage = %s", volumeUuid, uid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, Process.myUid(), hostPkg)) as Long
    }

    fun queryStatsForPackage(
        methodHandle: MethodHandle, volumeUuid: String?, packageName: String?,
        userId: Int, callingPackage: String?
    ): StorageStats? {
        logger.i("queryStatsForPackage > volumeUuid = %s, packageName = %s, userId = %s, callingPackage = %s",
            volumeUuid, packageName, userId, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg, userId, hostPkg)) as StorageStats?
    }

    fun queryStatsForUid(methodHandle: MethodHandle, volumeUuid: String?, uid: Int, callingPackage: String?): StorageStats? {
        logger.i("queryStatsForUid > volumeUuid = %s, uid = %s, callingPackage = %s", volumeUuid, uid, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, Process.myUid(), hostPkg)) as StorageStats?
    }

    fun queryStatsForUser(methodHandle: MethodHandle, volumeUuid: String?, userId: Int, callingPackage: String?): StorageStats? {
        logger.i("queryStatsForUid > volumeUuid = %s, userId = %s, callingPackage = %s", volumeUuid, userId, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, userId, hostPkg)) as StorageStats?
    }

    fun queryExternalStatsForUser(
        methodHandle: MethodHandle,
        volumeUuid: String?,
        userId: Int,
        callingPackage: String?
    ): ExternalStorageStats? {
        logger.i("queryExternalStatsForUser > volumeUuid = %s, userId = %s, callingPackage = %s", volumeUuid, userId, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, userId, hostPkg)) as ExternalStorageStats?
    }

    fun  /* CrateInfo */queryCratesForPackage(
        methodHandle: MethodHandle, volumeUuid: String?, packageName: String?,
        userId: Int, callingPackage: String?
    ): ParceledListSlice<*>? {
        logger.i("queryCratesForPackage > volumeUuid = %s, userId = %s, callingPackage = %s", volumeUuid, userId, callingPackage)
        return methodHandle.invokeOriginMethod(arrayOf<Any?>(volumeUuid, hostPkg, userId, hostPkg)) as ParceledListSlice<*>?
    }

    fun  /* CrateInfo */queryCratesForUid(
        methodHandle: MethodHandle, volumeUuid: String?, uid: Int,
        callingPackage: String?
    ): ParceledListSlice<*>? {
        logger.i("queryCratesForUid > volumeUuid = %s, uid = %s, callingPackage = %s", volumeUuid, Process.myUid(), callingPackage)
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                volumeUuid, uid, hostPkg
            )
        ) as ParceledListSlice<*>?
    }

    fun  /* CrateInfo */queryCratesForUser(
        methodHandle: MethodHandle, volumeUuid: String?, userId: Int,
        callingPackage: String?
    ): ParceledListSlice<*>? {
        return methodHandle.invokeOriginMethod(
            arrayOf<Any?>(
                volumeUuid, userId, hostPkg
            )
        ) as ParceledListSlice<*>?
    }
}