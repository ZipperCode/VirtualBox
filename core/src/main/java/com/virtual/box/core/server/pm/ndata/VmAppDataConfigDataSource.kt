package com.virtual.box.core.server.pm.ndata

import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.core.util.forEach
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.entity.VmAppDataConfigInfo
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class VmAppDataConfigDataSource {

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_APP_DATA_CONFIG_INFO)

    private val userAppDataMap = SparseArray<MutableList<VmAppDataConfigInfo>>()

    fun initData(userIdList: List<Int>){
        for (userId in userIdList) {
            val key = genAppDataKey(userId)
            val userAppDataList = ParcelDataHelper.loadList(iDataStorage, key, VmAppDataConfigInfo::class.java)
            userAppDataMap.put(userId, userAppDataList.toMutableList())
        }
    }

    fun saveAppDataConf(userId: Int, vmAppDataConfigInfo: VmAppDataConfigInfo){
        synchronized(userAppDataMap){
            try {
                var userAppDataList = userAppDataMap.get(userId)
                if (userAppDataList == null){
                    userAppDataList = mutableListOf()
                }
                userAppDataList.add(vmAppDataConfigInfo)
            }catch (e: Exception){
                throw e
            } finally {
                syncDataWithLock(userId)
            }
        }
    }

    fun removeAppDataConf(userId: Int, appDataId: String){
        synchronized(userAppDataMap){
            if (!userAppDataMap.containsKey(userId)){
                return
            }
            if (appDataId.isEmpty()){
                return
            }
            try {
                val iterator = userAppDataMap.get(userId).iterator()
                while (iterator.hasNext()){
                    val data = iterator.next()
                    if (data.appDataId == appDataId){
                        iterator.remove()
                        break
                    }
                }
            }catch (e: Exception){
                throw e
            } finally {
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