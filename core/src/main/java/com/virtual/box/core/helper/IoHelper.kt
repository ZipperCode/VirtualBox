package com.virtual.box.core.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import com.virtual.box.base.ext.checkAndCreate
import com.virtual.box.base.ext.checkAndMkdirs
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import com.virtual.box.reflect.android.content.pm.HPackageInfo
import java.io.File
import java.lang.Exception
import java.util.HashMap

object IoHelper {
    @SuppressLint("SdCardPath")
    fun enableRedirect(context: Context, packageInfo: ApplicationInfo) {
        val packageName = context.packageName
        val rule: MutableMap<String, String> = HashMap()
        try {
            val installAppDir = HApplicationInfo.scanPublicSourceDir.get(packageInfo)
//            val appOatDir = File(installAppDir,"oat").absolutePath
//            when {
//                packageInfo.nativeLibraryDir.contains("arm64") -> {
//                    rule["/data/app/${packageName}/lib/arm64"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/arm64"] = File(appOatDir,"arm64").absolutePath
//                }
//                packageInfo.nativeLibraryDir.contains("arm") -> {
//                    rule["/data/app/${packageName}/lib/arm"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/arm"] = File(appOatDir,"arm").absolutePath
//                }
//                packageInfo.nativeLibraryDir.contains("x86") -> {
//                    rule["/data/app/${packageName}/lib/x86"] = packageInfo.nativeLibraryDir
//                    rule["/data/app/${packageName}/oat/x86"] = File(appOatDir,"x86").absolutePath
//                }
//            }
//            rule["/data/app/$packageName/oat"] = appOatDir
//            rule["/data/app/$packageName/lib"] = HApplicationInfo.nativeLibraryRootDir.get(packageInfo)
            rule["/data/app/$packageName"] = installAppDir

            // /data/data
            rule["/data/data/$packageName"] = packageInfo.dataDir
            rule["/data/user/0/$packageName"] = packageInfo.dataDir

            if (VirtualBox.get().hostContext.externalCacheDir != null && context.externalCacheDir != null) {
                val external = context.externalCacheDir!!.parentFile!!
                // sdcard
                rule["/sdcard/Android/data/$packageName"] = external.absolutePath
                rule["/sdcard/android/data/$packageName"] = external.absolutePath
                rule["/storage/emulated/0/android/data/$packageName"] = external.absolutePath
                rule["/storage/emulated/0/Android/data/$packageName"] = external.absolutePath
//                rule["/storage/emulated/0/Android/data/$packageName/files"] =
//                    File(external.absolutePath, "files").absolutePath
//                rule["/storage/emulated/0/Android/data/$packageName/cache"] =
//                    File(external.absolutePath, "cache").absolutePath
            }
        }catch (e: Exception){
            L.printStackTrace(e)
        }
        val originPaths = ArrayList<String>(rule.size)
        val targetPaths = ArrayList<String>(rule.size)
        for (key in rule.keys) {
            originPaths.add(key)
            targetPaths.add(rule[key]!!)
            // 如果映射文件不存在，这边需要创建文件夹
            File(rule[key]!!).checkAndMkdirs()
        }
        VmCore.addIoRules(originPaths.toTypedArray(), targetPaths.toTypedArray())
    }
}