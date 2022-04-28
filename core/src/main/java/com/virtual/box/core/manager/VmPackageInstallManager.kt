package com.virtual.box.core.manager

import android.content.pm.PackageInfo
import com.virtual.box.base.ext.deleteDir
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.helper.PackageHelper
import java.io.File

internal object VmPackageInstallManager {
    private val logger = Logger.getLogger(L.SERVER_TAG, "VmPackageInstallManager")

    fun installVmPackageAsUser(vmPackageInfo: PackageInfo,filePath:String, userId: Int){
        createVmPackageEnv(vmPackageInfo,filePath, userId)
        PackageHelper.fixInstallApplicationInfo(vmPackageInfo.applicationInfo)
        val packageInfoFile = VmFileSystem.getInstallAppPackageInfoFile(vmPackageInfo.packageName)
        PackageHelper.saveInstallPackageInfo(vmPackageInfo, packageInfoFile)
    }

    fun uninstallVmPackageAsUser(packageName: String, userId: Int){
        deleteInstallDir(packageName, userId)
    }

    fun checkPackageInstalled(packageName: String): Boolean{
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

    private fun deleteInstallDir(packageName: String, userId: Int){
        // 删除安装目录
        VmFileSystem.getAppInstall(packageName).deleteDir()
        // 删除数据目录
        VmFileSystem.getDataDir(packageName, userId).deleteDir()
    }

    private fun createVmPackageEnv(vmPackageInfo: PackageInfo,filePath: String, userId: Int) {
        val packageName = vmPackageInfo.packageName
        initInstallEnv(vmPackageInfo, userId)
        val originFile = File(filePath)
        // 拷贝apk文件
        originFile.copyTo(VmFileSystem.getInstallBaseApkFile(packageName))
        // 拷贝so库
        PackageHelper.copyLibrary(originFile, VmFileSystem.getInstallAppLibDir(packageName))
    }

}