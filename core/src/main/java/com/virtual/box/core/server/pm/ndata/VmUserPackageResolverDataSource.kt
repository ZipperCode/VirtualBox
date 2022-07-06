package com.virtual.box.core.server.pm.ndata

import android.util.SparseArray
import androidx.annotation.WorkerThread
import androidx.core.util.containsKey
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.server.pm.VmComponentResolver
import com.virtual.box.core.server.pm.resolve.VmPackage

class VmUserPackageResolverDataSource {
    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_RESOLVED_PKG_DATA)

    private val vmUserResolver = SparseArray<VmComponentResolver>()

    @WorkerThread
    fun initData(userIdList: List<Int>) {
        for (userId in userIdList) {
            if (!vmUserResolver.containsKey(userId)) {
                val resolver = VmComponentResolver()
                val loadAllVmPackageResolver = loadAllVmPackageResolver(userId)
                for (vmPackage in loadAllVmPackageResolver) {
                    resolver.addAllComponents(vmPackage)
                }
                vmUserResolver.put(userId, resolver)
            }
        }
    }

    fun saveVmPackageResolver(userId: Int, vmPackage: VmPackage) {
        val packageName = vmPackage.packageName
        val key = getUserPkgKey(userId, packageName)
        var resolver: VmComponentResolver? = vmUserResolver.get(userId)
        if (resolver == null){
            resolver = VmComponentResolver()
            vmUserResolver.put(userId, resolver)
        }
        iDataStorage.save(key, vmPackage)
        resolver.addAllComponents(vmPackage)
    }

    fun removeVmPackageResolver(userId: Int, packageName: String){
        val resolver = vmUserResolver.get(userId)
        if (resolver != null){
            val key = getUserPkgKey(userId, packageName)
            iDataStorage.remove(key)
        }
    }

    fun loadAllVmPackageResolver(userId: Int): List<VmPackage> {
        val keys = iDataStorage.keys()
        val userPkKeys = keys.filter {
            it.startsWith(String.format(PACKAGE_RESOLVER_PREF_FORMAT, userId))
        }.toList()
        if (userPkKeys.isEmpty()){
            return emptyList()
        }
        val result = ArrayList<VmPackage>(userPkKeys.size)
        for (userPkKey in userPkKeys) {
            val data = iDataStorage.load(userPkKey, VmPackage::class.java)
            if (data != null){
                result.add(data)
            }
        }
        return result
    }

    private fun getUserPkgKey(userId: Int, packageName: String): String {
        return String.format(PACKAGE_RESOLVER_KEY_FORMAT, userId, packageName)
    }

    companion object {
        const val PACKAGE_RESOLVER_PREF_FORMAT = "resolved_pkg_%s_"
        const val PACKAGE_RESOLVER_KEY_FORMAT = "resolved_pkg_%s_%s"
    }
}