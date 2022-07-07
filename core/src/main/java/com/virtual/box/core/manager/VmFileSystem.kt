package com.virtual.box.core.manager

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.virtual.box.base.ext.checkAndMkdirs
import com.virtual.box.base.ext.deleteDir
import com.virtual.box.base.ext.deleteFile
import com.virtual.box.base.util.AppExecutors
import java.io.File
import java.util.*
@SuppressLint("StaticFieldLeak")
internal object VmFileSystem {

    const val SYSTEM_USER_ID = 0

    const val USER_INFO_CONFIG_NAME = "user"
    const val PACKAGE_INFO_CONFIG_NAME = "package"

    const val INSTALL_PACKAGE_INFO_FILE_NAME = "packageInfo.conf"

    const val INSTALL_APK_FILE_NAME = "base.apk"

    private lateinit var mContext: Context

    private lateinit var mVirtualRoot: File

    /**
     * {vmDir}/data/app/
     */
    private lateinit var mAppsInstallDir: File

    /**
     * {vmDir}/data/user/
     */
    private lateinit var mUsersSpaceDir: File

    /**
     * {vmDir}/data/data/
     */
    private lateinit var mUserAppsDataDir: File

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
        mAppsInstallDir = File(mVirtualRoot, "data/app/")
        mUsersSpaceDir = File(mVirtualRoot,"data/user/")
        mUserAppsDataDir = File(mVirtualRoot,"data/data/")

        mAppsInstallDir.checkAndMkdirs()
        mUsersSpaceDir.checkAndMkdirs()
        mUserAppsDataDir.checkAndMkdirs()

        mUserInfoConfig = File(mVirtualRoot,"user.conf")
        mInstallPackageInfoConfig = File(mVirtualRoot, "install.conf")
        isInit = true
    }

    /**
     * 应用安装目录，区分不同用户的安装位置
     * {host}/data/app/{userId}/{package}
     */
    fun getInstallAppRootDir(packageName: String, userId: Int): File{
        return File(mAppsInstallDir, String.format(Locale.CHINA,"%s/%s", userId, packageName))
    }

    /**
     * {host}/data/app/{userId}/{package}/lib
     */
    fun getAppInstallLibDir(packageName: String, userId: Int): File{
        return File(mAppsInstallDir, String.format(Locale.CHINA,"%s/%s/lib/", userId, packageName))
    }

    fun mkdirAppInstallDir(packageName: String, userId: Int){
        val installAppDir = getInstallAppRootDir(packageName, userId)
        File(installAppDir,"lib").checkAndMkdirs()
        File(installAppDir, "oat").checkAndMkdirs()
    }

    fun handleInstallDir(packageName: String, userId: Int){
        val installAppDir = getInstallAppRootDir(packageName, userId)
        if (installAppDir.exists()){
            installAppDir.deleteDir()
        }
        File(installAppDir,"lib").checkAndMkdirs()
        File(installAppDir, "oat").checkAndMkdirs()
    }

    fun handleUnInstallDir(packageName: String, userId: Int){
        val installAppDir = getInstallAppRootDir(packageName, userId)
        if (installAppDir.exists()){
            installAppDir.deleteDir()
        }
    }

    /**
     * {host}/data/app/{userId}/{package}/base.apk
     */
    fun getAppInstallBaseApkFile(packageName: String, userId: Int): File{
        return File(getInstallAppRootDir(packageName, userId),"base.apk")
    }

    fun getInstallAppPackageInfoFile(packageName: String, userId: Int):File{
        return File(getInstallAppRootDir(packageName, userId),INSTALL_PACKAGE_INFO_FILE_NAME)
    }

    /**
     * {host}/data/data/{userId}/{package}
     */
    fun getAppDataDir(packageName: String, userId: Int): File{
        return File(mUserAppsDataDir,String.format(Locale.CHINA,"%s/%s", userId, packageName))
    }

    fun getAppDataDir(packageName: String, userId: Int, suffix: String): File{
        return File(mUserAppsDataDir,String.format(Locale.CHINA,"%s/%s-%s", userId, packageName, suffix))
    }

    @MainThread
    fun mkdirAppDataDirAsync(packageName: String, userId: Int, suffix: String){
        AppExecutors.get().execute {
            val appDataDir = getAppDataDir(packageName, userId, suffix)
            File(appDataDir, "files").checkAndMkdirs()
            File(appDataDir, "cache").checkAndMkdirs()
            File(appDataDir, "code_cache").checkAndMkdirs()
            File(appDataDir, "lib").checkAndMkdirs()
            File(appDataDir, "databases").checkAndMkdirs()
            File(appDataDir, "shared_prefs").checkAndMkdirs()
            File(appDataDir, "oat").checkAndMkdirs()
        }
    }

    @MainThread
    fun mkdirAppDataDirAsync(rootDir: File){
        AppExecutors.get().execute {
            File(rootDir, "files").checkAndMkdirs()
            File(rootDir, "cache").checkAndMkdirs()
            File(rootDir, "code_cache").checkAndMkdirs()
            File(rootDir, "lib").checkAndMkdirs()
            File(rootDir, "databases").checkAndMkdirs()
            File(rootDir, "shared_prefs").checkAndMkdirs()
            File(rootDir, "oat").checkAndMkdirs()
        }
    }

    @MainThread
    fun removeAppDataDirAsync(packageName: String, userId: Int, suffix: String){
        AppExecutors.get().execute {
            val appDataDir = getAppDataDir(packageName, userId, suffix)
            File(appDataDir, "files").deleteDir()
            File(appDataDir, "cache").deleteDir()
            File(appDataDir, "code_cache").deleteDir()
            File(appDataDir, "lib").deleteDir()
            File(appDataDir, "databases").deleteDir()
            File(appDataDir, "shared_prefs").deleteDir()
            File(appDataDir, "oat").deleteDir()
        }
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

    fun getInstallAppLibAbiDir(packageName: String, abi: String): File{
        return File(getAppInstall(packageName), "lib/$abi")
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



    fun checkPackageAbi(packageName: String, abi: String): Boolean {
        val installAppLibAbiDir = getInstallAppLibAbiDir(packageName, abi)
        return installAppLibAbiDir.exists()
    }

    fun handleNewInstallAppDir(packageName: String, userId: Int){

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
        getDeDataDir(packageName, userId).checkAndMkdirs()
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