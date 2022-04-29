package com.virtual.box.core.server.pm.entity

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * 用户空间配置信息
 * @author zhangzhipeng
 * @date   2022/4/29
 **/
class VmPackageUserSpaceConfigInfo : Parcelable {
    /**
     * 用户id
     */
    val userId: Int

    /**
     * 用户存储空间配置
     */
    val packageUserSpace: HashMap<String, VmPackageUserSpace> = HashMap(20)

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        parcel.readMap(packageUserSpace, HashMap::class.java.classLoader)
    }

    constructor(userId: Int){
        this.userId = userId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeMap(packageUserSpace)
    }

    fun addPackageUserSpace(vmPackageUserSpace: VmPackageUserSpace){
        val packageName = vmPackageUserSpace.packageName
        // 添加新的，不需要删除旧的，key相同直接覆盖
        packageUserSpace[packageName] = vmPackageUserSpace
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmPackageUserSpaceConfigInfo> {
        override fun createFromParcel(parcel: Parcel): VmPackageUserSpaceConfigInfo {
            return VmPackageUserSpaceConfigInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmPackageUserSpaceConfigInfo?> {
            return arrayOfNulls(size)
        }
    }


}