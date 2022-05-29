package com.virtual.box.core.server.user

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.user.entity.VmUserInfo

class VmUserInfoRepo {

    private val iDataStorage: IDataStorage = ParcelDataHelper
        .getDataStorageLock(StorageConstant.VM_USER_INFO_NAME)

    fun exists(userId: Int): Boolean{
        return iDataStorage.containKey("$userId")
    }

    fun createUser(userId: Int){
        if (exists(userId)){
            return
        }
        val userInfo = VmUserInfo().apply {
            this.userId = userId
            this.createTime = System.currentTimeMillis()
        }
        iDataStorage.save("$userId", userInfo)
    }

    fun deleteUser(userId: Int){
        if (exists(userId)){
            iDataStorage.remove("$userId")
        }
    }
}