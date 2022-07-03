package com.virtual.box.core.server.pm.ndata

import android.content.pm.PackageInfo
import com.virtual.box.base.storage.IDataStorage

class VmNewPackageInfoDataSource(
    val dataStorage: IDataStorage
) {

    fun saveInstallVmPackageLock(vmPackageInfo: PackageInfo){
        synchronized(dataStorage){
            val packageName = vmPackageInfo.packageName
            dataStorage.save(packageName, vmPackageInfo)
        }
    }

    fun loadInstallVmPackageInfoLock(packageName: String): PackageInfo?{
        synchronized(dataStorage){
            return dataStorage.load(packageName, PackageInfo::class.java)
        }
    }

    fun removeInstallVmPackageInfoLock(packageName: String){
        synchronized(dataStorage){
            dataStorage.remove(packageName)
        }
    }
}