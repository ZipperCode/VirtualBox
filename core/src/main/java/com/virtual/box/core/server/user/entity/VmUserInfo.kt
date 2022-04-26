package com.virtual.box.core.server.user.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * 用户信息类
 * @author zhangzhipeng
 * @date   2022/4/26
 **/
class VmUserInfo() : Parcelable {
    /**
     * 用户Id userId
     */
    var userId: Int = 0
    /**
     * 创建时间
     */
    var createTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userId = parcel.readInt()
        createTime = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeLong(createTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmUserInfo> {
        override fun createFromParcel(parcel: Parcel): VmUserInfo {
            return VmUserInfo(parcel)
        }

        override fun newArray(size: Int): Array<VmUserInfo?> {
            return arrayOfNulls(size)
        }
    }
}