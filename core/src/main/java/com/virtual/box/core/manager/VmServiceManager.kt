package com.virtual.box.core.manager

import android.os.IBinder
import com.virtual.box.core.server.am.VmActivityManagerService
import com.virtual.box.core.server.pm.VmPackageManagerService
import java.util.HashMap

internal object VmServiceManager {
    const val PACKAGE_MANAGER = "package_manager"
    const val ACTIVITY_MANAGER = "activity_manager"

    /**
     * 服务进程的服务管理对象
     */
    private val vmServiceCaches: MutableMap<String, IBinder> = HashMap(10)

    fun initService(){
        vmServiceCaches[PACKAGE_MANAGER] = VmPackageManagerService
        vmServiceCaches[ACTIVITY_MANAGER] = VmActivityManagerService
    }

    @JvmStatic
    fun getService(name: String): IBinder?{
        return vmServiceCaches[name]
    }

}