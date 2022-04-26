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


    fun initSystem(context: Context){
        mContext = context
        mVirtualRoot = File(mContext.filesDir, "virtual")
        mVirtualRoot.checkAndMkdirs()
        mUserInfoConfig = File(mVirtualRoot,"user.conf")
    }

    /**
     * 应用的安装目录
     */
    fun getAppInstall(packageName: String): File{
        return File(mVirtualRoot, String.format(Locale.CHINA,"data/app/%s"))
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

    fun getInstallDir(packageName: String): File{
        return File(mVirtualRoot, "data/app/$packageName")
    }

    fun getInstallBaseApkFile(packageName: String): File {
        return File(getInstallDir(packageName), "base.apk")
    }

    fun getInstallAppLibDir(packageName: String): File {
        return File(getInstallDir(packageName), "lib")
    }

    fun getInstallAppOatDir(packageName: String): File{
        return File(getInstallDir(packageName), "oat")
    }

}