package com.virtual.box.core.server.pm.entity

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.server.pm.ndata.VmInstallPackageConfigDataSource
import java.io.File

/**
 * 安装包信息类
 *
 * 存储安装的应用包配置信息
 *
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmPackageConfigInfo: Parcelable {
    /**
     * 包名
     */
    val packageName: String

    var installOption: VmPackageInstallOption? = null

    var installPackageApkFilePath: String

    var installPackageInfoFilePath: String

    /**
     * 安装包版本号
     */
    var installPackageInfoVersionCode: Long

    /**
     * 安装包版本名
     */
    var installPackageInfoVersionCodeName: String

    var appId: Int = -1

    constructor(vmPackageInfo: PackageInfo, vmPackageInstallOption: VmPackageInstallOption){
        this.packageName = vmPackageInfo.packageName
        this.installOption = vmPackageInstallOption
        this.installPackageApkFilePath = vmPackageInfo.applicationInfo.sourceDir!!
        this.installPackageInfoFilePath = File(vmPackageInfo.applicationInfo.publicSourceDir).parent!! + File.separator + VmFileSystem.INSTALL_PACKAGE_INFO_FILE_NAME
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            this.installPackageInfoVersionCode = vmPackageInfo.longVersionCode
        }else{
            this.installPackageInfoVersionCode = vmPackageInfo.versionCode.toLong()
        }
        this.installPackageInfoVersionCodeName = vmPackageInfo.versionName
    }

    constructor(parcel: Parcel){
        this.packageName = parcel.readString() ?: ""
        this.installOption = parcel.readParcelable(VmPackageInstallOption::class.java.classLoader)
        this.installPackageApkFilePath = parcel.readString()?: VmFileSystem.getInstallBaseApkFile(packageName).absolutePath
        this.installPackageInfoFilePath = parcel.readString()?: VmFileSystem.getInstallAppPackageInfoFile(packageName).absolutePath
        this.installPackageInfoVersionCode = parcel.readLong()
        this.installPackageInfoVersionCodeName = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(packageName)
        parcel.writeParcelable(installOption, flags)
        parcel.writeString(installPackageApkFilePath)
        parcel.writeString(installPackageInfoFilePath)
        parcel.writeLong(installPackageInfoVersionCode)
        parcel.writeString(installPackageInfoVersionCodeName)
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
    }

    private fun registerAppIdLPw(){

    }
}