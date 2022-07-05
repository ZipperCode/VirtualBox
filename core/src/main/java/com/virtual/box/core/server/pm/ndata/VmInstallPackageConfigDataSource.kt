package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo

class VmInstallPackageConfigDataSource {

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_INSTALL_PKG_CONFIG_INFO)

    fun addInstallPackageConfig(userId: Int, vmPackageConfigInfo: VmPackageConfigInfo){
        val key = String.format(INSTALL_VALUE_KEY_FORMAT, userId, vmPackageConfigInfo.packageName)
        iDataStorage.save(key, vmPackageConfigInfo)
    }

    fun getInstallPackageConfig(userId: Int, packageName: String): VmPackageConfigInfo?{
        val key = String.format(INSTALL_VALUE_KEY_FORMAT, userId, packageName)
        return getInstallPackageConfig(key)
    }

    fun getInstallPackageConfig(key: String): VmPackageConfigInfo?{
        return iDataStorage.load(key, VmPackageConfigInfo::class.java)
    }

    companion object{
        const val INSTALL_VALUE_KEY_FORMAT = "install_pkg_%s_%s"
    }

}