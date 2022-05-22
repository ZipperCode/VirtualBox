package com.virtual.box.core.server.pm.data

import android.content.pm.PackageParser
import androidx.annotation.WorkerThread
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.resolve.VmPackage

class VmPackageResolverDataSource {

    private val installVmPackageResolverStorage: IDataStorage
        = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_PK_RESOLVER_INFO)

    private val lock = Any()

    @WorkerThread
    fun addVmPackageResolverLock(vmPackage: VmPackage){
        synchronized(lock){
            val packageName = vmPackage.packageName
            if (installVmPackageResolverStorage.containKey(packageName)){
                installVmPackageResolverStorage.remove(packageName)
            }
            installVmPackageResolverStorage.save(packageName, vmPackage)
        }
    }

    @WorkerThread
    fun removeVmPackageResolverLock(packageName: String) {
        synchronized(lock) {
            installVmPackageResolverStorage.remove(packageName)
        }
    }

    fun checkPackageResolverExists(packageName: String): Boolean{
        return installVmPackageResolverStorage.containKey(packageName)
    }

    fun loadVmPackageResolverLock(packageName: String): VmPackage?{
        return installVmPackageResolverStorage.load(packageName, VmPackage::class.java)
    }

    @WorkerThread
    fun loadAllVmPackageResolverLock(): Map<String, VmPackage>{
        synchronized(lock){
            val keys = installVmPackageResolverStorage.keys()
            val result = HashMap<String, VmPackage>(keys.size)
            for (key in keys) {
                val vmPackage = installVmPackageResolverStorage.load(key,VmPackage::class.java) ?: continue
                result[key] = vmPackage
            }
            return result
        }
    }

    companion object{
        const val PackageResolverInfoStorageName = "PackageResolverInfoStorageName"
    }
}