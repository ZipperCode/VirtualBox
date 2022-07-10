package com.virtual.box.core.server.pm.ndata

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo

class VmInstallPackageConfigDataSource {

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_INSTALL_PKG_CONFIG_INFO)

    fun saveInstallPackageConfig(userId: Int, vmPackageConfigInfo: VmPackageConfigInfo){
        val key = String.format(INSTALL_VALUE_KEY_FORMAT, userId, vmPackageConfigInfo.packageName)
        iDataStorage.save(key, vmPackageConfigInfo)
    }

    fun removePackageConfig(userId: Int, packageName: String){
        val key = getKey(userId, packageName)
        iDataStorage.remove(key)
    }

    fun getInstallPackageConfig(userId: Int, packageName: String): VmPackageConfigInfo?{
        val key = String.format(INSTALL_VALUE_KEY_FORMAT, userId, packageName)
        return getInstallPackageConfig(key)
    }

    fun getInstallPackageConfig(key: String): VmPackageConfigInfo?{
        return iDataStorage.load(key, VmPackageConfigInfo::class.java)
    }

    fun checkPackageConfExists(userId: Int, packageName: String):Boolean{
        val key = getKey(userId, packageName)
        return iDataStorage.containKey(key)
    }

    fun getUserAllPackageUserConfList(userId: Int): List<VmPackageConfigInfo>{
        val filterKey = iDataStorage.keys().filter {
            it.startsWith(String.format(INSTALL_VALUE_PREF_FORMAT, userId))
        }.toList()
        val result = ArrayList<VmPackageConfigInfo>(filterKey.size)
        for (key in filterKey) {
            val data = iDataStorage.load(key, VmPackageConfigInfo::class.java)
            if (data != null){
                result.add(data)
            }
        }
        return result
    }

    private fun getKey(userId: Int, packageName: String): String{
        return  String.format(INSTALL_VALUE_KEY_FORMAT, userId, packageName)
    }

    companion object{
        const val INSTALL_VALUE_PREF_FORMAT = "install_pkg_%s_"
        const val INSTALL_VALUE_KEY_FORMAT = "install_pkg_%s_%s"
        const val UID_KEY_FORMAT = "uids"
    }

}