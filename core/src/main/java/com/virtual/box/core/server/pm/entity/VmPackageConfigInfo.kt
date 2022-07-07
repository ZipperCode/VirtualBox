package com.virtual.box.core.server.pm.entity

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.server.pm.ndata.VmInstallPackageConfigDataSource
import java.io.File
import java.util.*

/**
 * 安装包信息类
 *
 * 存储安装的应用包配置信息
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
data class VmPackageConfigInfo(
    /**
     * 安装包的唯一id，主要用来与appData.installPackageId对应
     * 应用安装时确定
     */
    val packageId: String,
    /**
     * 包所属的用户Id
     */
    val userId: Int,
    /**
     * 包名
     */
    val packageName: String,
    /**
     * 安装包版本号
     */
    var installPackageInfoVersionCode: Long,
    /**
     * 安装包版本名
     */
    var installPackageInfoVersionCodeName: String,
    /**
     * 应用根目录（base.apk的父目录）
     */
    val installPackageRootPath:String,
    /**
     * 安装时的参数
     */
    var installOption: VmPackageInstallOption?,
    /**
     * 应用id，与用户无关，多用户下，如果存在相同的appId，即同一个应用，与PackageName同理
     */
    var appId: Int = -1,

    /**
     * 最后更新时间
     */
    var lastUpdateTime: Long = 0
): Parcelable {

    constructor(vmPackageInfo: PackageInfo, vmPackageInstallOption: VmPackageInstallOption, userId: Int, appId: Int): this(
        packageId = UUID.randomUUID().toString(),
        userId = userId,
        packageName = vmPackageInfo.packageName,
        installPackageInfoVersionCode = getVersionCode(vmPackageInfo),
        installPackageInfoVersionCodeName = vmPackageInfo.versionName,
        installPackageRootPath = File(vmPackageInfo.applicationInfo.publicSourceDir).parent!!,
        installOption = vmPackageInstallOption,
    ){
    }

    constructor(parcel: Parcel): this(
        packageId = parcel.readString()!!,
        userId = parcel.readInt(),
        packageName = parcel.readString()!!,
        installPackageInfoVersionCode = parcel.readLong(),
        installPackageInfoVersionCodeName = parcel.readString()!!,
        installPackageRootPath = parcel.readString()!!,
        installOption = parcel.readParcelable(VmPackageInstallOption::class.java.classLoader),
        appId = parcel.readInt(),
        lastUpdateTime = parcel.readLong()
    ){
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageId)
        parcel.writeInt(userId)
        parcel.writeString(packageName)
        parcel.writeLong(installPackageInfoVersionCode)
        parcel.writeString(installPackageInfoVersionCodeName)
        parcel.writeString(installPackageRootPath)
        parcel.writeParcelable(installOption, flags)
        parcel.writeInt(appId)
        parcel.writeLong(lastUpdateTime)
    }

    val installPackageInfoFilePath: String get(){
        return File(installPackageRootPath, VmFileSystem.INSTALL_APK_FILE_NAME).absolutePath
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageConfigInfo> {
        override fun createFromParcel(parcel: Parcel): VmPackageConfigInfo {
            return VmPackageConfigInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageConfigInfo?> {
            return arrayOfNulls(size)
        }

        private fun getVersionCode(vmPackageInfo: PackageInfo): Long{
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { vmPackageInfo.longVersionCode }else{ vmPackageInfo.versionCode.toLong() }
        }
    }
}