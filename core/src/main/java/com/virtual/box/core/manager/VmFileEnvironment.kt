package com.virtual.box.core.manager

import android.annotation.SuppressLint
import android.content.Context
import com.virtual.box.base.ext.checkAndMkdirs
import java.io.File
import java.util.*
@SuppressLint("StaticFieldLeak")
internal object VmFileEnvironment {

    const val USER_INFO_CONFIG_NAME = "user"
    const val PACKAGE_INFO_CONFIG_NAME = "package"

    private lateinit var mContext: Context

    private lateinit var mVirtualRoot: File

    lateinit var mUserInfoConfig: File
        private set

    lateinit var mInstallPackageInfoConfig: File

    fun initSystem(context: Context){
        mContext = context
        mVirtualRoot = File(mContext.filesDir, "virtual")
        mVirtualRoot.checkAndMkdirs()
        mUserInfoConfig = File(mVirtualRoot,"user.conf")
        mInstallPackageInfoConfig = File(mVirtualRoot, "install.conf")
    }

    /**
     * 应用的安装目录
     */
    fun getAppInstall(packageName: String): File{
        return File(mVirtualRoot, String.format(Locale.CHINA,"data/app/%s"))
    }

    fun getInstallBaseApkFile(packageName: String): File {
        return File(getAppInstall(packageName), "base.apk")
    }

    fun getInstallAppLibDir(packageName: String): File {
        return File(getAppInstall(packageName), "lib")
    }

    fun getInstallAppArmLibDir(packageName: String): File {
        return File(getAppInstall(packageName), "lib/arm")
    }

    fun getInstallAppArm64LibDir(packageName: String): File {
        return File(getAppInstall(packageName), "lib/arm64")
    }

    fun getInstallAppOatDir(packageName: String): File{
        return File(getAppInstall(packageName), "oat")
    }

    /**
     * 用户空间目录
     */
    fun getUserDir(userId: Int):File{
        return File(mVirtualRoot, String.format(Locale.CHINA, "data/user/%d", userId))
    }

    /**
     * 应用的私有数据目录
     */
    fun getDataDir(packageName: String, userId: Int): File {
        return File(mVirtualRoot, String.format(Locale.CHINA, "data/user/%d/%s", userId, packageName))
    }

    fun getDeDataDir(packageName: String, userId: Int): File {
        return File(mVirtualRoot, String.format(Locale.CHINA, "data/user_de/%d/%s", userId, packageName))
    }

    fun checkArm64(packageName: String): Boolean{
        val installAppArm64LibDir = getInstallAppArm64LibDir(packageName)
        return installAppArm64LibDir.exists() && installAppArm64LibDir.listFiles()?.isNotEmpty() == true
    }

    fun mkdirAppInstall(packageName: String){
        val installDir = getAppInstall(packageName)
        if (!installDir.exists()){
            return
        }
        File(installDir, "lib").checkAndMkdirs()
        File(installDir, "oat").checkAndMkdirs()
    }

    fun mkdirAppData(packageName: String, userId: Int){
        val appDataDir = getDataDir(packageName, userId)
        if (!appDataDir.exists()){
            return
        }
        File(appDataDir, "files").checkAndMkdirs()
        File(appDataDir, "cache").checkAndMkdirs()
        File(appDataDir, "lib").checkAndMkdirs()
        File(appDataDir, "databases").checkAndMkdirs()
        File(appDataDir, "shared_prefs").checkAndMkdirs()
        File(appDataDir, "oat").checkAndMkdirs()
    }


}