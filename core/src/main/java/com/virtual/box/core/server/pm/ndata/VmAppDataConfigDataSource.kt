package com.virtual.box.core.server.pm.ndata

import android.util.SparseArray
import androidx.core.util.forEach
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.entity.VmAppDataConfigInfo
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class VmAppDataConfigDataSource {

    private val keyDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_USER_PKG_CONFIG_INFO)

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_APP_DATA_CONFIG_INFO)

    private val userAppDataMap = SparseArray<MutableList<VmAppDataConfigInfo>>()

    fun initData(userIdList: List<Int>){
        for (userId in userIdList) {
            val key = genAppDataKey(userId)
            val userAppDataList = ParcelDataHelper.loadList(iDataStorage, key, VmAppDataConfigInfo::class.java)
            userAppDataMap.put(userId, userAppDataList.toMutableList())
        }
    }

    fun saveAppDataConf(userId: Int, vmAppDataConfigInfo: VmAppDataConfigInfo): String{
        synchronized(userAppDataMap){
            try {
                var userAppDataList = userAppDataMap.get(userId)
                if (userAppDataList == null){
                    userAppDataList = mutableListOf()
                }
                if (vmAppDataConfigInfo.appDataId.isNullOrEmpty()){
                    vmAppDataConfigInfo.appDataId = UUID.randomUUID().toString().replace("-","")
                }
                userAppDataList.add(vmAppDataConfigInfo)
                return vmAppDataConfigInfo.appDataId!!
            }finally {
                syncDataWithLock(userId)
            }
        }
    }

    private fun genAppDataKey(userId: Int): String{
        return String.format(APP_DATA_KEY_FORMAT, userId)
    }

    private fun genAppDataValueKey(userId: Int, packageName: String): String{
        return String.format(APP_DATA_VALUE_KEY_FORMAT, userId, packageName,
            UUID.randomUUID().toString().replace("-",""))
    }

    private fun syncDataWithLock(userId: Int){
        synchronized(userAppDataMap){
            val userAppDataList = userAppDataMap.get(userId)
            if (userAppDataList != null){
                ParcelDataHelper.saveList(iDataStorage, genAppDataKey(userId), userAppDataList)
            }
        }
    }

    private fun syncDataWithLock(){
        synchronized(userAppDataMap){
            userAppDataMap.forEach { key, value ->
                ParcelDataHelper.saveList(iDataStorage, genAppDataKey(key), value)
            }
        }
    }

    companion object{
        const val APP_DATA_KEY_FORMAT = "app_data_%s"
        const val APP_DATA_VALUE_KEY_FORMAT = "app_data_%s_%s_%s"
    }
}