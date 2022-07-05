package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmUserPackageDataSource {

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_USER_PKG_CONFIG_INFO)

    /**
     * 安装信息存储成功后，将key保存
     */
    fun addInstallPackageKey(userId: Int, value: String){
        saveKey(String.format(INSTALL_KEY_FORMAT,userId), value)
    }

    /**
     * 创建用户储存配置后，将key保存
     */
    fun addAppDataKey(userId: Int, value: String){
        saveKey(String.format(APP_DATA_KEY_FORMAT,userId), value)
    }

    fun getInstallKeyList(userId: Int): List<String>{
        return loadKeys(String.format(INSTALL_KEY_FORMAT, userId))
    }

    fun getAppDataKeyList(userId: Int): List<String>{
        return loadKeys(String.format(APP_DATA_KEY_FORMAT, userId))
    }

    fun checkInstallKeysExists(userId: Int, packageName: String): Boolean{
        val installKeyList = getInstallKeyList(userId)
        return installKeyList.contains(String.format(INSTALL_VALUE_KEY_FORMAT, userId, packageName))
    }

    fun checkAppDataKeysExists(userId: Int, packageName: String): Boolean{
        val appDataKeyList = getAppDataKeyList(userId)
        return appDataKeyList.contains(String.format(APP_DATA_VALUE_KEY_FORMAT, userId, packageName))
    }

    private fun saveKey(key: String, value: String){
        val keys = iDataStorage.load(key, emptySet()).toMutableSet()
        keys.add(value)
        iDataStorage.save(key, keys)
    }

    private fun loadKeys(key: String): List<String>{
        return iDataStorage.load(key, emptySet()).toList()
    }

    companion object{
        const val INSTALL_KEY_FORMAT = "install_pkg_%s"
        const val INSTALL_VALUE_KEY_FORMAT = "install_pkg_%s_%s"

        const val APP_DATA_KEY_FORMAT = "app_data_%s"
        const val APP_DATA_VALUE_KEY_FORMAT = "app_data_%s_%s"
    }

}