package com.virtual.box.core.manager

import android.annotation.SuppressLint
import android.content.Context
import com.virtual.box.base.ext.checkAndMkdirs
import java.io.File
import java.util.*
@SuppressLint("StaticFieldLeak")
internal object VmFileSystem {

    const val SYSTEM_USER_ID = 0

    const val USER_INFO_CONFIG_NAME = "user"
    const val PACKAGE_INFO_CONFIG_NAME = "package"

    const val INSTALL_PACKAGE_INFO_FILE_NAME = "packageInfo.conf"

    private lateinit var mContext: Context

    private lateinit var mVirtualRoot: File

    lateinit var mUserInfoConfig: File
        private set

    lateinit var mInstallPackageInfoConfig: File

    private var isInit = false

    fun initSystem(context: Context){
        if (isInit){
            return
        }
        mContext = context.applicationContext
        mVirtualRoot = File(mContext.filesDir, "virtual")
        mVirtualRoot.checkAndMkdirs()
        mUserInfoConfig = File(mVirtualRoot,"user.conf")
        mInstallPackageInfoConfig = File(mVirtualRoot, "install.conf")
        isInit = true
    }

    /**
     * 应用的安装目录
     */
    fun getAppInstall(packageName: String): File{
        return File(mVirtualRoot, String.format(Locale.CHINA,"data/app/%s", packageName))
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

    fun getInstallAppPackageInfoFile(packageName: String): File{
        return File(getAppInstall(packageName), INSTALL_PACKAGE_INFO_FILE_NAME)
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
    fun getUserDataDir(packageName: String, userId: Int): File {
        return File(mVirtualRoot, String.format(Locale.CHINA, "data/user/%d/%s", userId, packageName))
    }

    fun getDataDir(packageName: String, userId: Int): File{
        return if (userId == SYSTEM_USER_ID){
            File(mVirtualRoot, String.format("data/data/%s", packageName))
        }else{
            getUserDataDir(packageName, userId)
        }
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
            installDir.checkAndMkdirs()
        }
        File(installDir, "lib").checkAndMkdirs()
        File(installDir, "oat").checkAndMkdirs()
    }

    fun mkdirAppData(packageName: String, userId: Int){
//        val userAppDataDir = getUserDataDir(packageName, userId)
//        if (!userAppDataDir.exists()){
//            userAppDataDir.checkAndMkdirs()
//        }
//        mkdirAppData(userAppDataDir)

        val dataDir = getDataDir(packageName, userId)
        dataDir.checkAndMkdirs()
        mkdirAppData(dataDir)
    }

    private fun mkdirAppData(appDataDir: File){
        File(appDataDir, "files").checkAndMkdirs()
        File(appDataDir, "cache").checkAndMkdirs()
        File(appDataDir, "code_cache").checkAndMkdirs()
        File(appDataDir, "lib").checkAndMkdirs()
        File(appDataDir, "databases").checkAndMkdirs()
        File(appDataDir, "shared_prefs").checkAndMkdirs()
        File(appDataDir, "oat").checkAndMkdirs()
    }


}