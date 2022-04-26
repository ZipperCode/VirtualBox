package com.virtual.box.core.manager

import android.os.Bundle
import android.os.IBinder
import androidx.core.app.BundleCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox

object ServiceManager {

//    private val logger = Logger.getLogger(L.VM_TAG, "ServiceManager")
//    /**
//     * 自定义系统服务
//     */
//    private val vmService: MutableMap<String, IBinder> = HashMap()
//
//    fun initService() {
//        if (VirtualBox.get().isServerProcess){
//            return
//        }
//        logger.d("非服务进程，初始化服务进程的代理对象")
//        initServiceInternal()
//        listenServer()
//    }
//
//    /**
//     * 非服务进程，初始化自定义服务（和虚拟进程通信服务）
//     */
//    private fun initServiceInternal(){
//        // 自定义的窗口管理服务 aidl
//        getService(BServiceManager.ACTIVITY_MANAGER)
//        // 包管理服务 aidl
//        getService(BServiceManager.PACKAGE_MANAGER)
//        // 存储服务 aidl
//        getService(BServiceManager.STORAGE_MANAGER)
//    }
//
//    /**
//     * 主进程监控server进程
//     */
//    private fun listenServer(){
//        // 主进程监控server进程
//        if (VirtualCore.get().isMainProcess){
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
//
//
//    /**
//     * 服务获取服务进程的代理对象
//     *
//     * @param name 服务端代理对象名称
//     * @return 拿到的是Server进程的自定义服务代理对象
//     */
//    fun getService(name: String): IBinder? {
//        var binder = mServices[name]
//        if (binder != null && binder.isBinderAlive) {
//            L.vd("服务端进程binder对象还未消亡 ${binder.isBinderAlive}")
//            return binder
//        }
//        val bundle = Bundle()
//        bundle.putString(SystemCallProvider.VM_SERVICE_NAME_KEY, name)
//        val vm = ProviderCallHelper.callSafely(ProxyManifest.bindProvider, SystemCallProvider.VM_METHOD_NAME, null, bundle)!!
//        // 取出服务端放置的binder服务
//        binder = BundleCompat.getBinder(vm, SystemCallProvider.VM_SERVICE_BINDER_KEY)
//        binder?.apply {
//            mServices[name] = binder
//        }
//        return binder
//    }
}