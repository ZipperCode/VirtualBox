package com.virtual.box.core.server.pm.data

import android.content.pm.PackageInfo
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmPackageInfoDataSource {

    private val installVmPackageInfoStorage: IDataStorage
            = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_PK_INFO)

    fun saveInstallVmPackageLock(vmPackageInfo: PackageInfo){
        synchronized(installVmPackageInfoStorage){
            val packageName = vmPackageInfo.packageName
            installVmPackageInfoStorage.save(packageName, vmPackageInfo)
        }
    }

    fun loadInstallVmPackageInfoLock(packageName: String): PackageInfo?{
        synchronized(installVmPackageInfoStorage){
            return installVmPackageInfoStorage.load(packageName, PackageInfo::class.java)
        }
    }

    fun removeInstallVmPackageInfoLock(packageName: String){
        synchronized(installVmPackageInfoStorage){
            installVmPackageInfoStorage.remove(packageName)
        }
    }

}