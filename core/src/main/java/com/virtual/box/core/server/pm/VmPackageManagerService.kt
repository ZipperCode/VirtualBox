package com.virtual.box.core.server.pm

import android.content.pm.PackageInfo
import android.content.pm.PackageParser
import android.os.Process
import com.virtual.box.base.ext.isNotNullOrEmpty
import com.virtual.box.base.util.AppExecutors
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.compat.PackageParserCompat
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.manager.VmPackageInstallManager
import com.virtual.box.core.manager.VmProcessManager
import com.virtual.box.core.server.pm.entity.*
import com.virtual.box.core.server.user.VmUserManagerService
import com.virtual.box.reflect.android.content.pm.HPackageParser
import java.io.File
import kotlin.Exception

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
internal class VmPackageManagerService private constructor() : IVmPackageManagerService.Stub() {
    private val logger = Logger.getLogger(L.SERVER_TAG, "BPackageManagerService")

    companion object {
        const val TAG = "BPackageManagerService"
        private var sService: VmPackageManagerService? = null

        @JvmStatic
        fun get(): VmPackageManagerService {
            if (sService == null) {
                synchronized(this) {
                    if (sService == null) {
                        sService = VmPackageManagerService()
                    }
                }
            }
            return sService!!
        }
    }

    /**
     * 跨进程监听包监听
     */
    private val packageObservers: MutableList<IVmPackageObserver> = ArrayList(10)

    /**
     * 安装线程锁
     */
    private val mInstallLock = Any()

    private val vmPackageRepo: VmPackageRepo = VmPackageRepo()

    override fun registerPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || packageObservers.contains(observer) || observer.asBinder()?.isBinderAlive == false) {
            return
        }
        packageObservers.add(observer)
    }

    override fun unregisterPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || !packageObservers.contains(observer) || observer.asBinder()?.isBinderAlive == false) {
            return
        }
        packageObservers.remove(observer)
    }

    override fun installPackageAsUser(installOptions: VmPackageInstallOption?, userId: Int): VmPackageResult {
        if (installOptions == null) {
            return VmPackageResult.installFail( "install fial installOptions == null")
        }
        logger.method("【IPC】开始安装应用 >> install = %s, userId = %s", installOptions, userId)
        val tid = Process.myTid()
        val threadId = Thread.currentThread().id
        logger.method("IPC >> 调用服务进程进行包安装", "tid = %s, threadId = %s, option = %s, userId = %s", tid, threadId, installOptions, userId)
        return installPackageAsUserLocked(installOptions, userId)
    }


    override fun installPackageAsUserAsync(installOptions: VmPackageInstallOption?, userId: Int): Int {
        if (installOptions == null) {
            logger.e("【IPC】异步安装应用失败，installOptions == null")
            return -1
        }
        logger.i("【IPC】开始异步安装应用 >> install = %s, userId = %s", installOptions, userId)
        AppExecutors.get().doBackground{
            installPackageAsUserLocked(installOptions, userId)
        }
        return 1
    }

    private fun installPackageAsUserLocked(option: VmPackageInstallOption, userId: Int): VmPackageResult {
        if (!option.checkOriginFlag()) {
            return VmPackageResult.installFail("安装失败，安装flag错误，flag = ${option.originFlags}")
        }
        val start = System.currentTimeMillis()
        val filePath = if (option.isOriginFlag(VmPackageInstallOption.FLAG_STORAGE)) {
            val file = File(option.filePath)
            if (!file.exists()) {
                return VmPackageResult.installFail("安装失败，文件路径不存在：${option.filePath}")
            }
            option.filePath
        } else {
            if (!option.packageName.isNotNullOrEmpty()) {
                return VmPackageResult.installFail("安装失败，packageName == null")
            }
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(option.packageName, 0);
            } catch (e: Exception) {
                return VmPackageResult.installFail("安装失败，未找到对应的package = ${option.packageName}")
            }
            packageInfo.applicationInfo.publicSourceDir
        }
        val installResult = VmPackageResult()
        AppExecutors.get().executeMultiThreadWithLock {
            // 解析apk文件包
            val aPackage = parserApk(filePath)
            if (aPackage == null) {
                installResult.msg = "解析apk文件：${filePath}失败"
                return@executeMultiThreadWithLock
            }
            logger.i("调用包安装服务进行安装：成功，保存安装数据到本地")
            val hostPackageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(VirtualBox.get().hostPkg, 0)
            val vmPackageInfo = PackageHelper.getInstallPackageInfo(hostPackageInfo, aPackage)
//            val vmPackageInfo = PackageHelper.getInstallPackageInfoByFile(VirtualBox.get().hostContext, filePath)
//            if (vmPackageInfo == null) {
//                installResult.msg = "解析apk文件：${filePath}失败"
//                return@executeMultiThreadWithLock
//            }
            VmUserManagerService.checkOrCreateUser(userId)
            // 停止同包名下的应用进程
            VmProcessManager.killProcess(vmPackageInfo.packageName, userId)
            // 安装处理
            VmPackageInstallManager.installVmPackageAsUser(vmPackageInfo, filePath, userId)
            // 安装包配置
            val vmPackageSetting = VmPackageConfigInfo(vmPackageInfo, option)
            // 保存安装信息
            vmPackageRepo.addInstallPackageInfoWithLock(vmPackageSetting)
            installResult.packageName = vmPackageInfo.packageName
            installResult.success = true
            logger.i("应用包安装成功 end = ${System.currentTimeMillis() - start}")
        }
        return installResult
    }

    override fun uninstallPackageAsUser(packageName: String?, userId: Int): VmPackageResult {
        synchronized(mInstallLock){
            if (!isInstalled(packageName, userId)){
                logger.e("【IPC】用户卸载安装包失败，校验不通过")
                return VmPackageResult.installFail("用户卸载安装包失败，校验不通过")
            }
            logger.i("【IPC】正在卸载用户安装包")
            // 关闭应用进程
            VmProcessManager.killProcess(packageName!!, userId)
            // 删除用户安装配置
            VmPackageInstallManager.uninstallVmPackageAsUser(packageName, userId)
            // 如果没有用户安装了此安装包，删除安装路径
        }
        return VmPackageResult.installSuccess(packageName!!)
    }

    override fun isInstalled(packageName: String?, userId: Int): Boolean {
        if (packageName.isNullOrEmpty()){
            logger.e("【IPC】检查是否安装，packageName == null")
            return false
        }

        if (!VmUserManagerService.exists(userId)){
            logger.e("【IPC】检查是否安装，用户不存在 userId = %s", userId)
            return false
        }

        if (!vmPackageRepo.checkPackageInstalled(packageName)){
            logger.e("【IPC】检查是否安装，未找到安装包配置")
            return false
        }

        if (!VmPackageInstallManager.checkPackageInstalled(packageName)){
            logger.e("【IPC】检查是否安装，未找到安装包文件：packageInfo.conf")
            return false
        }

        return true
    }

    override fun getVmInstalledPackageInfo(flag: Int): MutableList<VmInstalledPackageInfo> {
        logger.i("【IPC】查找安装的应用")
        val installPackageInfoList = vmPackageRepo.getPackageInfoList(flag)
        return installPackageInfoList.map { VmInstalledPackageInfo(0, it.packageName, it) }.toMutableList()
    }

    private fun dispatcherPackageInstall(installResult: VmPackageResult){
        checkPackageObserver()
        for (packageObserver in packageObservers) {
            packageObserver.onPackageResult(installResult)
        }
    }

    private fun checkPackageObserver(){
        val iterator = packageObservers.iterator()
        while (iterator.hasNext()){
            val iter = iterator.next()
            if (!iter.asBinder().isBinderAlive){
                iterator.remove()
            }
        }
    }

    /**
     * 解析apk包
     * @param file
     * @return
     */
    private fun parserApk(file: String): PackageParser.Package? {
        try {
            val parser = HPackageParser.constructor.newInstance()
            val aPackage = parser.parsePackage(File(file), 0)
            PackageParserCompat.collectCertificates(parser, aPackage, 0)
            return aPackage
        } catch (t: Throwable) {
            logger.e(t)
        }
        return null
    }


}