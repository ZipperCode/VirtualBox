package com.virtual.box.core.server.pm.ndata

import android.content.pm.PackageInfo
import android.os.Process
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant

class VmInstallPackageInfoDataSource {
    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_INSTALL_PKG_DATA)

    /**
     * package.uid => Process.FIRST_APPLICATION_UID until Process.LAST_APPLICATION_UID
     */
    private val usedVmUidList = mutableSetOf<String>()

    fun initData(){
        val uids = iDataStorage.load(UID_KEY_FORMAT, emptySet())
        usedVmUidList.clear()
        usedVmUidList.addAll(uids)
    }

    private fun generateUid(): Int{
        for (i in Process.FIRST_APPLICATION_UID until Process.LAST_APPLICATION_UID){
            if (!usedVmUidList.contains(i.toString())){
                return i
            }
        }
        return -1
    }

    fun savePackageInfo(userId: Int, vmPackageInfo: PackageInfo){
        val key = getKey(userId, vmPackageInfo.packageName)
        val uid = generateUid()
        vmPackageInfo.applicationInfo.uid = uid
        syncLocalUidData()
        iDataStorage.save(key, vmPackageInfo)
    }

    fun removePackageInfo(userId: Int, packageName: String){
        val key = getKey(userId, packageName)
        val packageInfo = iDataStorage.load(key, PackageInfo::class.java)
        val uid = packageInfo?.applicationInfo?.uid ?: -1
        if (uid != -1){
            usedVmUidList.remove(uid.toString())
            syncLocalUidData()
        }
        iDataStorage.remove(key)
    }

    fun loadInstallVmPackageInfo(packageName: String, userId: Int): PackageInfo?{
        val key = getKey(userId, packageName)
        return iDataStorage.load(key, PackageInfo::class.java)
    }

    private fun getKey(userId: Int, packageName: String): String{
        return String.format(PACKAGE_INFO_KEY_FORMAT, userId, packageName)
    }

    private fun syncLocalUidData(){
        synchronized(this){
            iDataStorage.save(UID_KEY_FORMAT, usedVmUidList)
        }
    }

    companion object{
        const val PACKAGE_INFO_KEY_FORMAT = "install_pkg_%s_%s"

        const val UID_KEY_FORMAT = "uids"
    }
}