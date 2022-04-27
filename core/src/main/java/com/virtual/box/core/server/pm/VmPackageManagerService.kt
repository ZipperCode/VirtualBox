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

    private val userManager = VmUserManagerService

    /**
     * 安装线程锁
     */
    private val mInstallLock = Any()

    private val vmPackageRepo: VmPackageRepo = VmPackageRepo()

    override fun registerPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || packageObservers.contains(observer)) {
            return
        }
        packageObservers.add(observer)
    }

    override fun unregisterPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || !packageObservers.contains(observer)) {
            return
        }
        packageObservers.remove(observer)
    }

    override fun installPackageAsUser(installOptions: VmPackageInstallOption?, userId: Int): VmPackageInstallResult {
        if (installOptions == null) {
            return VmPackageInstallResult.installFail( "install fial installOptions == null")
        }
        logger.i("【IPC】开始安装应用 >> install = %s, userId = %s", installOptions, userId)
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

    private fun installPackageAsUserLocked(option: VmPackageInstallOption, userId: Int): VmPackageInstallResult {
        if (!option.checkOriginFlag()) {
            return VmPackageInstallResult.installFail("安装失败，安装flag错误，flag = ${option.originFlags}")
        }
        val filePath = if (option.isOriginFlag(VmPackageInstallOption.FLAG_STORAGE)) {
            val file = File(option.filePath)
            if (!file.exists()) {
                return VmPackageInstallResult.installFail("安装失败，文件路径不存在：${option.filePath}")
            }
            option.filePath
        } else {
            if (!option.packageName.isNotNullOrEmpty()) {
                return VmPackageInstallResult.installFail("安装失败，packageName == null")
            }
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(option.packageName, 0);
            } catch (e: Exception) {
                return VmPackageInstallResult.installFail("安装失败，未找到对应的package = ${option.packageName}")
            }
            packageInfo.applicationInfo.publicSourceDir
        }
        val installResult = VmPackageInstallResult()
        AppExecutors.get().executeMultiThreadWithLock {
            // 解析apk文件包
            val aPackage = parserApk(filePath)
            if (aPackage == null) {
                installResult.msg = "解析apk文件：${filePath}失败"
                return@executeMultiThreadWithLock
            }
            logger.i("调用包安装服务进行安装：成功，保存安装数据到本地")
            val hostPackageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(VirtualBox.get().hostPkg, 0)
            val vmPackageInfo = VmPackageInfo.convert(hostPackageInfo, aPackage)
            userManager.checkOrCreateUser(userId)
            // 停止同包名下的应用进程
            VmProcessManager.killProcess(userId)
            // 安装处理
            VmPackageInstallManager.installVmPackageAsUser(vmPackageInfo, userId)
            // 安装包配置
            val vmPackageSetting = VmPackageSetting(vmPackageInfo, option)
            vmPackageRepo.addPackageSetting(vmPackageSetting)
            installResult.packageName = aPackage.packageName
            installResult.success = true
        }
        return installResult
    }

    override fun uninstallPackageAdUser(packageName: String?, userId: Int): Int {
        if (!isInstalled(packageName, userId)){
            logger.e("【IPC】用户卸载安装包失败，校验不通过")
            return -1
        }
        synchronized(mInstallLock){
            // 关闭应用进程

            // 删除用户安装配置

            // 如果没有用户安装了此安装包，删除安装路径
        }
        return 0
    }

    override fun isInstalled(packageName: String?, userId: Int): Boolean {
        if (packageName.isNullOrEmpty()){
            logger.e("【IPC】检查是否安装，packageName == null")
            return false
        }

        if (!userManager.exists(userId)){
            logger.e("【IPC】检查是否安装，用户不存在 userId = %s", userId)
            return false
        }

        if (!vmPackageRepo.checkPackageInfo(packageName)){
            logger.e("【IPC】检查是否安装，未找到安装包配置")
            return false
        }

        if (!VmPackageInstallManager.checkPackageInstalled(packageName)){
            logger.e("【IPC】检查是否安装，未找到安装包文件：packageInfo.conf")
            return false
        }

        return true
    }


    private fun dispatcherPackageInstall(installResult: VmPackageInstallResult){
        for (packageObserver in packageObservers) {
            packageObserver.onPackageResult(installResult)
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