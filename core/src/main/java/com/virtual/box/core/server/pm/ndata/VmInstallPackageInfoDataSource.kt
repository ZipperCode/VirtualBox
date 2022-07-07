package com.virtual.box.core.server.pm.ndata

import android.content.pm.PackageInfo
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmInstallPackageInfoDataSource {
    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_INSTALL_PKG_DATA)

    fun savePackageInfo(userId: Int, vmPackageInfo: PackageInfo){
        val key = getKey(userId, vmPackageInfo.packageName)
        iDataStorage.save(key, vmPackageInfo)
    }

    fun removePackageInfo(userId: Int, packageName: String){
        val key = getKey(userId, packageName)
        iDataStorage.remove(key)
    }

    fun loadInstallVmPackageInfo(packageName: String, userId: Int): PackageInfo?{
        val key = getKey(userId, packageName)
        return iDataStorage.load(key, PackageInfo::class.java)
    }

    private fun getKey(userId: Int, packageName: String): String{
        return String.format(PACKAGE_INFO_KEY_FORMAT, userId, packageName)
    }

    companion object{
        const val PACKAGE_INFO_KEY_FORMAT = "install_pkg_%s_%s"
    }
}