package com.virtual.box.core.entity

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import java.lang.IllegalArgumentException

class VmProxyServiceRecord(
    val userId: Int,
    val originIntent: Intent?,
    val serviceInfo: ServiceInfo?,
    val token: IBinder,
    val startId: Int
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Intent::class.java.classLoader),
        parcel.readParcelable(ServiceInfo::class.java.classLoader),
        parcel.readStrongBinder(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeParcelable(originIntent, flags)
        parcel.writeParcelable(serviceInfo, flags)
        parcel.writeStrongBinder(token)
        parcel.writeInt(startId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VmProxyServiceRecord> {
        override fun createFromParcel(parcel: Parcel): VmProxyServiceRecord {
            return VmProxyServiceRecord(parcel)
        }

        override fun newArray(size: Int): Array<VmProxyServiceRecord?> {
            return arrayOfNulls(size)
        }

        private const val IPC_PROXY_SERVICE_RECORD = "IPC_PROXY_SERVICE_RECORD"

        fun saveStubInfo(shadowIntent: Intent, targetIntent: Intent, serviceInfo: ServiceInfo, token: IBinder, userId: Int, startId: Int){
            val serviceRecord = VmProxyServiceRecord(userId, targetIntent, serviceInfo, token, startId)
            shadowIntent.putExtra(IPC_PROXY_SERVICE_RECORD, serviceRecord)
        }

        fun parseStubInfo(shadowIntent: Intent): VmProxyServiceRecord {
            return shadowIntent.getParcelableExtra(IPC_PROXY_SERVICE_RECORD)
                ?: throw IllegalArgumentException("parse stub service record fail, parse null");
        }
    }
}