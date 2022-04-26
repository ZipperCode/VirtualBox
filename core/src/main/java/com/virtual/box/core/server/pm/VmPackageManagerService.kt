package com.virtual.box.core.server.pm

import android.content.pm.PackageInfo
import android.content.pm.PackageParser
import android.os.IBinder
import android.os.Process
import com.virtual.box.base.ext.isNotNullOrEmpty
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.compat.PackageParserCompat
import com.virtual.box.core.hook.core.VmCore
import com.virtual.box.core.server.pm.entity.VmPackageInfo
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import com.virtual.box.core.server.pm.entity.VmPackageInstallResult
import com.virtual.box.core.server.user.VmUserManagerService
import com.virtual.box.reflect.android.content.pm.HPackageParser
import java.io.File
import kotlin.Exception
import kotlin.jvm.Throws

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
internal class VmPackageManagerService private constructor(): IVmPackageManagerService.Stub() {
    private val logger = Logger.getLogger(L.SERVER_TAG,"BPackageManagerService")

    companion object {
        const val TAG = "BPackageManagerService"
        private var sService: VmPackageManagerService? = null

        @JvmStatic
        fun get(): VmPackageManagerService {
            if (sService == null){
                synchronized(this){
                    if (sService == null){
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

    override fun registerPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || packageObservers.contains(observer)){
            return
        }
        packageObservers.add(observer)
    }

    override fun unregisterPackageObserver(observer: IVmPackageObserver?) {
        if (observer == null || !packageObservers.contains(observer)){
            return
        }
        packageObservers.remove(observer)
    }

    override fun installPackageAsUser(installOptions: VmPackageInstallOption?, userId: Int): VmPackageInstallResult {
        if (installOptions == null){
            return VmPackageInstallResult().apply {
                success = false
                msg = "install fial installOptions == null"
            }
        }

        val tid = Process.myTid()
        val threadId = Thread.currentThread().id
        logger.method("IPC >> 调用服务进程进行包安装","tid = %s, threadId = %s, option = %s, userId = %s",tid, threadId, installOptions, userId)
        synchronized(mInstallLock) {
            return installPackageAsUserLocked(installOptions, userId)
        }

    }

    override fun installPackageAsUserAsync(installOptions: VmPackageInstallOption?, userId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun uninstallPackageAdUser(packageName: String?, userId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isInstalled(packageName: String?, userId: Int): Boolean {
        TODO("Not yet implemented")
    }

    private fun installPackageAsUserLocked(option: VmPackageInstallOption, userId: Int): VmPackageInstallResult {
        if (!option.checkFlag()){
            return VmPackageInstallResult.installFail("安装失败，安装flag错误，flag = ${option.flags}")
        }
        val filePath = if (option.isFlag(VmPackageInstallOption.FLAG_STORAGE)){
            val file = File(option.filePath)
            if (!file.exists()){
                return VmPackageInstallResult.installFail("安装失败，文件路径不存在：${option.filePath}")
            }
            option.filePath
        }else{
            if (!option.packageName.isNotNullOrEmpty()){
                return VmPackageInstallResult.installFail("安装失败，packageName == null")
            }
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(option.packageName, 0);
            }catch (e: Exception){
                return VmPackageInstallResult.installFail("安装失败，未找到对应的package = ${option.packageName}")
            }
            packageInfo.applicationInfo.publicSourceDir
        }
        // 解析apk文件包
        val aPackage = parserApk(filePath) ?: return VmPackageInstallResult.installFail("解析apk文件：${filePath}失败")
        logger.i("调用包安装服务进行安装：成功，保存安装数据到本地")
        val hostPackageInfo = VirtualBox.get().hostContext.packageManager.getPackageInfo(VirtualBox.get().hostPkg,0)
        val vmPackageInfo = VmPackageInfo.convert(hostPackageInfo, aPackage)
        // 停止同包名下的应用进程

        userManager.checkOrCreateUser(userId)
        val installResult = VmPackageInstallResult()
        installResult.packageName = aPackage.packageName

        return VmPackageInstallResult()
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