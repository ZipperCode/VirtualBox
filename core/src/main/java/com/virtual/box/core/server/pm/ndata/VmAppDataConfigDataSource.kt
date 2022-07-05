package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmAppDataConfigDataSource {
    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_APP_DATA_CONFIG_INFO)

    companion object{
        const val APP_DATA_VALUE_KEY_FORMAT = "app_data_%s_%s"
    }
}