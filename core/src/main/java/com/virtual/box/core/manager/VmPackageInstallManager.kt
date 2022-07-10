package com.virtual.box.core.manager

import android.content.pm.PackageInfo
import android.os.Debug
import com.virtual.box.base.ext.checkAndMkdirs
import com.virtual.box.base.ext.deleteDir
import com.virtual.box.base.ext.deleteFile
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.compat.ComponentFixCompat
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.server.pm.ndata.VmPackageRepo
import com.virtual.box.core.server.user.BUserHandle
import java.io.File
import java.lang.Exception

internal object VmPackageInstallManager {
    private val logger = Logger.getLogger(L.SERVER_TAG, "VmPackageInstallManager")

    fun installBaseVmPackage(vmPackageInfo: PackageInfo, filePath: String, userId: Int){
        val packageName = vmPackageInfo.packageName
        logger.i("安装包到指定目录，user = %s, packageName = %s, filePath = %s", userId, packageName, filePath)
        VmFileSystem.handleInstallDir(packageName, userId)
        val originFile = File(filePath)
        // 拷贝apk文件
        val installBaseApkFile = VmFileSystem.getInstallBaseApkFile(packageName)
        installBaseApkFile.deleteFile()
        originFile.copyTo(installBaseApkFile)
        // 拷贝so库
        PackageHelper.copyLibrary(originFile, VmFileSystem.getInstallAppLibDir(packageName))
        val packageAbi = PackageHelper.getPackageCpuAbi(packageName, userId)
        // 修复application
        PackageHelper.fixInstallApplicationInfo(vmPackageInfo.applicationInfo, packageAbi)
        // 保存安装包配置文件
        val packageInfoFile = VmFileSystem.getInstallAppPackageInfoFile(packageName, userId)
        PackageHelper.saveInstallPackageInfoAsync(vmPackageInfo, packageInfoFile)
    }

    fun unInstallBasePackage(packageName: String){
        // 删除安装目录
        VmFileSystem.getAppInstall(packageName).deleteDir()
    }

    fun uninstallVmPackageAsUserData(packageName: String, userId: Int){
        // 删除数据目录
        VmFileSystem.getUserDataDir(packageName, userId).deleteDir()
        VmFileSystem.getDeDataDir(packageName, userId).deleteDir()
    }

    fun checkPackageInstalled(packageName: String): Boolean {
        return VmFileSystem.getInstallAppPackageInfoFile(packageName).exists()
    }

    private fun initInstallEnv(vmPackageInfo: PackageInfo, userId: Int) {
        val packageName = vmPackageInfo.packageName
        deleteInstallDir(packageName, userId)
        // 创建安装目录
        VmFileSystem.mkdirAppInstall(packageName)
        // 创建数据目录
        VmFileSystem.mkdirAppData(packageName, userId)
    }

    private fun deleteInstallDir(packageName: String, userId: Int) {
        // 删除安装目录
        VmFileSystem.getAppInstall(packageName).deleteDir()
        // 删除数据目录
        VmFileSystem.getUserDataDir(packageName, userId).deleteDir()
    }

    private fun createVmPackageEnv(vmPackageInfo: PackageInfo, filePath: String, userId: Int) {
        val packageName = vmPackageInfo.packageName
        initInstallEnv(vmPackageInfo, userId)
        val originFile = File(filePath)
        // 拷贝apk文件
        originFile.copyTo(VmFileSystem.getInstallBaseApkFile(packageName))
        // 拷贝so库
        PackageHelper.copyLibrary(originFile, VmFileSystem.getInstallAppLibDir(packageName))
    }

    fun deleteUserSpaceData(packageName: String, userId: Int) {
        VmFileSystem.getDataDir(packageName, userId).deleteDir()
    }

    fun uninstallVmPackage(packageName: String) {
        // 删除安装目录
        VmFileSystem.getAppInstall(packageName).deleteDir()
    }


}