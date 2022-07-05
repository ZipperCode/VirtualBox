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
    fun initData(userIdList: List<Int>){
        for (userId in userIdList) {
            if (!vmUserResolver.containsKey(userId)){
                val resolver = VmComponentResolver()
                val loadAllVmPackageResolver = loadAllVmPackageResolver(userId)
                for (vmPackage in loadAllVmPackageResolver) {
                    resolver.addAllComponents(vmPackage)
                }
                vmUserResolver.put(userId, resolver)
            }
        }
    }

    fun loadAllVmPackageResolver(userId: Int): List<VmPackage> {
        val key = String.format(PACKAGE_RESOLVER_KEY_FORMAT, userId)
        return ParcelDataHelper.loadList(iDataStorage, key, VmPackage::class.java)
    }

    companion object{
        const val PACKAGE_RESOLVER_KEY_FORMAT = "resolved_pkg_%s"
    }
}