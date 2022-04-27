package com.virtual.box.core.manager

import com.virtual.box.base.ext.deleteDir
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.server.pm.entity.VmPackageInfo
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import java.io.File

internal object VmPackageInstallManager {
    private val logger = Logger.getLogger(L.SERVER_TAG, "VmPackageInstallManager")

    fun installVmPackageAsUser(vmPackageInfo: VmPackageInfo, userId: Int){
        createVmPackageEnv(vmPackageInfo, userId)
        adjustInstallPackageInfo(vmPackageInfo, userId)
        val packageInfoFile = VmFileEnvironment.getInstallAppPackageInfoFile(vmPackageInfo.packageName)
        PackageHelper.saveInstallPackageInfo(vmPackageInfo, packageInfoFile)
    }

    fun uninstallVmPackageAsUser(packageName: String, userId: Int){
        deleteInstallDir(packageName, userId)
    }

    fun checkPackageInstalled(packageName: String): Boolean{
        return VmFileEnvironment.getInstallAppPackageInfoFile(packageName).exists()
    }

    private fun initInstallEnv(vmPackageInfo: VmPackageInfo, userId: Int) {
        val packageName = vmPackageInfo.packageName
        deleteInstallDir(packageName, userId)
        // 创建安装目录
        VmFileEnvironment.mkdirAppInstall(packageName)
        // 创建数据目录
        VmFileEnvironment.mkdirAppData(packageName, userId)
    }

    private fun deleteInstallDir(packageName: String, userId: Int){
        // 删除安装目录
        VmFileEnvironment.getAppInstall(packageName).deleteDir()
        // 删除数据目录
        VmFileEnvironment.getDataDir(packageName, userId).deleteDir()
    }

    private fun createVmPackageEnv(vmPackageInfo: VmPackageInfo, userId: Int) {
        val packageName = vmPackageInfo.packageName
        initInstallEnv(vmPackageInfo, userId)
        // 拷贝apk文件
        val pksOriginFile = vmPackageInfo.applicationInfo.publicSourceDir
        val originFile = File(pksOriginFile)
        originFile.copyTo(VmFileEnvironment.getInstallBaseApkFile(packageName))
        // 拷贝so库
        PackageHelper.copyLibrary(originFile, VmFileEnvironment.getInstallAppLibDir(packageName))
    }

    private fun adjustInstallPackageInfo(vmPackageInfo: VmPackageInfo,userId: Int){
        val packageName = vmPackageInfo.packageName
        val is64 = VmFileEnvironment.checkArm64(packageName)
        val abiName = if (is64){
            // 64
            "arm64-v8a"
        }else{
            "armeabi"
        }
        vmPackageInfo.applicationInfo?.apply {
            nativeLibraryDir = if (is64){
                VmFileEnvironment.getInstallAppArm64LibDir(packageName).absolutePath
            }else{
                VmFileEnvironment.getInstallAppArmLibDir(packageName).absolutePath
            }
            HApplicationInfo.primaryCpuAbi.set(this, abiName)
            HApplicationInfo.nativeLibraryRootDir.set(this, VmFileEnvironment.getInstallAppLibDir(packageName).absolutePath)
            val installFile = VmFileEnvironment.getInstallBaseApkFile(packageName)
            publicSourceDir = installFile.absolutePath
            sourceDir = installFile.absolutePath
            val installDir = VmFileEnvironment.getAppInstall(packageName)
            HApplicationInfo.scanPublicSourceDir.set(this,installDir.absoluteFile )
            HApplicationInfo.scanSourceDir.set(this,installDir.absoluteFile)
        }
    }

}