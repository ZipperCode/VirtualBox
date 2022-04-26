package com.virtual.box.core.manager

import android.os.IBinder
import java.util.HashMap

object VmServiceManager {
    const val PACKAGE_MANAGER = "package_manager"

    /**
     * 服务进程的服务管理对象
     */
    private val vmServiceCaches: MutableMap<String, IBinder> = HashMap(10)


    init {

    }

    @JvmStatic
    fun getService(name: String): IBinder?{
        return vmServiceCaches[name]
    }


}