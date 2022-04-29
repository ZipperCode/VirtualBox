package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.manager.VmPackageInstallManager
import com.virtual.box.core.server.user.VmUserManagerService
import java.io.File

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmPackageSettings(): Parcelable {
    /**
     * 安装包配置信息
     * 新的应用安装后，安装包信息会存储到这边
     * 包名：安装包信息
     */
    var packageSetting: HashMap<String, VmPackageConfigInfo> = HashMap(10)

    /**
     * 用户空间数据
     * 用户Id: 空间配置数据
     */
    var packageUserSpaceSetting: HashMap<Int, VmPackageUserSpaceConfigInfo> = HashMap(5)

    constructor(parcel: Parcel) : this() {
        parcel.readMap(packageSetting, HashMap::class.java.classLoader)
        parcel.readMap(packageUserSpaceSetting, HashMap::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeMap(packageSetting)
        parcel.writeMap(packageUserSpaceSetting)
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageSettings> {

        override fun createFromParcel(parcel: Parcel): VmPackageSettings {
            return VmPackageSettings(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageSettings?> {
            return arrayOfNulls(size)
        }

    }
}