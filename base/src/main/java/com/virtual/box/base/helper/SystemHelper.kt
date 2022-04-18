package com.virtual.box.base.helper

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.reflect.android.app.HActivityThread

object SystemHelper {
    /**
     * 当前进程名称
     */
    private lateinit var sCurrentProcessName: String

    @JvmStatic
    fun isMainProcess(): Boolean{
        return try {
            getCurrentProcessName() == getPackageName()
        }catch (e: Exception){
            false
        }
    }
    /**
     * 反色获取进程名，使用任务栈获取耗性能
     */
    @JvmStatic
    fun getCurrentProcessName(): String{
        synchronized(this){
            if (!SystemHelper::sCurrentProcessName.isInitialized){
                val curName = getProcessNameCompat()
                if (curName.isNullOrBlank()){
                    return ""
                }
                sCurrentProcessName = curName
            }
            return sCurrentProcessName
        }
    }

    fun getCurrentProcessNameExcludePackage(): String{
        return getCurrentProcessName().replace(getPackageName(),"")
    }

    @SuppressLint("NewApi")
    private fun getProcessNameCompat(): String?{
        return if (BuildCompat.isAtLeastPie){
            Application.getProcessName()
        }else{
            HActivityThread.currentProcessName.call() as String?
        }
    }

    fun getPackageName(): String{
        return HActivityThread.currentPackageName.call() as String? ?: ""
    }

    @JvmStatic
    fun is64Bit(): Boolean {
        val is64 =  if (BuildCompat.isAtLeastM) {
            Process.is64Bit()
        } else {
            Build.CPU_ABI == "arm64-v8a"
        }
        L.vd("是否是64位架构 >> $is64")
        return is64
    }

    @JvmStatic
    fun getPrimaryCpuAbiName(): String{
        return if (is64Bit()) "arm64-v8a" else "armeabi-v7a"
    }

    /**
     * 获取当前进程名称
     */
    @JvmStatic
    fun getProcessName(context: Context): String {
        val pid = Process.myPid()
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (info in am.runningAppProcesses) {
            if (info.pid == pid) {
                processName = info.processName
                break
            }
        }
        if (processName == null) {
            throw RuntimeException("processName = null")
        }
        return processName
    }
}