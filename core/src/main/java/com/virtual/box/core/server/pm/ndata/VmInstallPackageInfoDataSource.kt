package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmInstallPackageInfoDataSource {
    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_INSTALL_PKG_DATA)

    companion object{
        const val PACKAGE_INFO_KEY_FORMAT = "install_pkg_%s"
    }
}