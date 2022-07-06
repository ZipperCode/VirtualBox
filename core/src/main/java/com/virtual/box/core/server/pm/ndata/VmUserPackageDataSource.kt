package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

@Deprecated("")
class VmUserPackageDataSource {

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_USER_PKG_CONFIG_INFO)

    fun genInstallPackageKey(userId: Int):String{
        return String.format(INSTALL_KEY_FORMAT,userId)
    }

    fun genInstallPackageValueKey(userId: Int, packageName: String): String{
        return String.format(INSTALL_VALUE_KEY_FORMAT,userId, packageName)
    }

    companion object{
        const val INSTALL_KEY_FORMAT = "install_pkg_%s"
        const val INSTALL_VALUE_KEY_FORMAT = "install_pkg_%s_%s"

        const val APP_DATA_KEY_FORMAT = "app_data_%s"
        const val APP_DATA_VALUE_KEY_FORMAT = "app_data_%s_%s"
    }

}