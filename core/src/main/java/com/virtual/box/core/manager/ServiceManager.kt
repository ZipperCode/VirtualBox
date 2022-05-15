package com.virtual.box.core.manager

import android.app.IServiceConnection
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.BundleCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.helper.ProviderCallHelper
import com.virtual.box.core.proxy.ProxyManifest
import com.virtual.box.core.server.VmServiceProvider
import com.virtual.box.core.server.pm.IVmPackageManagerService
import com.virtual.box.core.service.BindDaemonService
import com.virtual.box.core.service.DaemonService

/**
 * 客户端使用
 */
object ServiceManager {

    private val logger = Logger.getLogger(L.VM_TAG, "ServiceManager")
    /**
     * 自定义系统服务
     */
    private val vmService: MutableMap<String, IBinder> = HashMap()

    private val serviceConnection: ServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            logger.d("服务连接成功")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            logger.d("服务连接断开")
        }
    }

    fun initService() {
        if (VirtualBox.get().isServerProcess){
            BindDaemonService.bindService(VirtualBox.get().hostContext, serviceConnection)
            return
        }
        logger.d("非服务进程，初始化服务进程的代理对象")
        initServiceInternal()
    }

    /**
     * 非服务进程，初始化自定义服务（和虚拟进程通信服务）
     */
    private fun initServiceInternal(){
        // 包管理服务 aidl
        getService(VmServiceManager.PACKAGE_MANAGER)
        getService(VmServiceManager.ACTIVITY_MANAGER)
    }
//
//    /**
//     * 主进程监控server进程
//     */
//    private fun listenServer(){
//        // 主进程监控server进程
//        if (VirtualBox.get().isMainProcess){
//            currentBActivityThread = currentBActivityThread()
//            currentBActivityThread?.linkToDeath({
//                L.ve("主线程与服务进程失去连接 >> 服务进程可能挂了 >> 重新初始化服务")
//                currentBActivityThread = null
//                reAttachServerProcess()
//            },0)
//        }
//    }
//
//    private fun reAttachServerProcess(){
//        if (currentBActivityThread == null){
//            currentBActivityThread = currentBActivityThread()
//            mServices.clear()
//            initService()
//        }
//    }

    /**
     * 服务获取服务进程的代理对象
     *
     * @param name 服务端代理对象名称
     * @return 拿到的是Server进程的自定义服务代理对象
     */
    fun getService(name: String): IBinder? {
        var binder = vmService[name]
        if (binder != null && binder.isBinderAlive) {
            L.vd("服务端进程binder对象还未消亡 ${binder.isBinderAlive}")
            return binder
        }
        val bundle = Bundle()
        bundle.putString(VmServiceProvider.VM_SERVICE_NAME_KEY, name)
        val vm = ProviderCallHelper.callSafely(ProxyManifest.serviceProvider, VmServiceProvider.VM_METHOD_NAME, null, bundle)!!
        // 取出服务端放置的binder服务
        binder = BundleCompat.getBinder(vm, VmServiceProvider.VM_SERVICE_BINDER_KEY)
        binder?.apply {
            vmService[name] = binder
        }
        return binder
    }
}