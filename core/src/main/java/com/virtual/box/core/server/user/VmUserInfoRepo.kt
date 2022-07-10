package com.virtual.box.core.server.user

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.user.entity.VmUserInfo

class VmUserInfoRepo {

    private val iDataStorage: IDataStorage = ParcelDataHelper
        .getDataStorageLock(StorageConstant.VM_USER_INFO_NAME)

    fun exists(userId: Int): Boolean{
        return iDataStorage.containKey(String.format(USER_KEY_PREFIX_FORMAT, userId))
    }

    fun createUser(userId: Int){
        synchronized(iDataStorage){
            if (exists(userId)){
                return
            }
            val userInfo = VmUserInfo(
                userId, System.currentTimeMillis()
            )
            iDataStorage.save(String.format(USER_KEY_PREFIX_FORMAT, userId), userInfo)
        }
    }

    fun deleteUser(userId: Int){
        synchronized(iDataStorage){
            if (exists(userId)){
                iDataStorage.remove(String.format(USER_KEY_PREFIX_FORMAT, userId))
            }
        }
    }

    fun loadUserInfo(userId: Int): VmUserInfo?{
        return iDataStorage.load(String.format(USER_KEY_PREFIX_FORMAT, userId), VmUserInfo::class.java)
    }

    fun loadUserKeys(): Set<Int>{
        val keys = iDataStorage.keys()
        return keys.map {
            try {
                it.replace("user_","").toInt()
            }catch (ignore: Exception){
                // SYSTEM_USER
                0
            }
        }.toSet()
    }

    fun loadAllUserWithLock(): List<VmUserInfo>{
        val keys = iDataStorage.keys()
        if (keys.isEmpty()){
            return emptyList()
        }
        return keys.mapNotNull {
            iDataStorage.load(it, VmUserInfo::class.java)
        }.toList()
    }

    companion object{
        const val USER_KEY_PREFIX_FORMAT = "user_%s"
    }
}