package com.virtual.box.core.server.pm.ndata

import android.os.Parcel
import android.os.Parcelable
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo
import com.virtual.box.core.server.pm.entity.VmPackageUserSpaceConfigInfo

class VmNewVmPackageSettings(
    val userId: Int
): Parcelable {
    /**
     * 已安装的包信息
     */
    val installPackageConfig: HashMap<String, VmPackageConfigInfo> = HashMap(20)

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        parcel.readMap(installPackageConfig, VmPackageConfigInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.readMap(installPackageConfig, VmPackageConfigInfo::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmNewVmPackageSettings> {
        override fun createFromParcel(parcel: Parcel): VmNewVmPackageSettings {
            return VmNewVmPackageSettings(parcel)
        }

        override fun newArray(size: Int): Array<VmNewVmPackageSettings?> {
            return arrayOfNulls(size)
        }
    }

}