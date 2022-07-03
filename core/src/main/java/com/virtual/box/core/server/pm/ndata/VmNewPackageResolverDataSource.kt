package com.virtual.box.core.server.pm.ndata

import androidx.annotation.WorkerThread
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.core.server.pm.resolve.VmPackage

class VmNewPackageResolverDataSource(
    val installVmPackageResolverStorage: IDataStorage
) {

    @WorkerThread
    fun addVmPackageResolverLock(vmPackage: VmPackage){
        synchronized(installVmPackageResolverStorage){
            val packageName = vmPackage.packageName
            if (installVmPackageResolverStorage.containKey(packageName)){
                installVmPackageResolverStorage.remove(packageName)
            }
            installVmPackageResolverStorage.save(packageName, vmPackage)
        }
    }

    @WorkerThread
    fun removeVmPackageResolverLock(packageName: String) {
        synchronized(installVmPackageResolverStorage) {
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
        synchronized(installVmPackageResolverStorage){
            val keys = installVmPackageResolverStorage.keys()
            val result = HashMap<String, VmPackage>(keys.size)
            for (key in keys) {
                val vmPackage = installVmPackageResolverStorage.load(key, VmPackage::class.java) ?: continue
                result[key] = vmPackage
            }
            return result
        }
    }
}