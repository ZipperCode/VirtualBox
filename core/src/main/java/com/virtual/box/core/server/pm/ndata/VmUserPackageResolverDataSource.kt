package com.virtual.box.core.server.pm.ndata

import android.content.Intent
import android.content.pm.*
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
        if (resolver == null) {
            resolver = VmComponentResolver()
            vmUserResolver.put(userId, resolver)
        }
        iDataStorage.save(key, vmPackage)
        resolver.addAllComponents(vmPackage)
    }

    fun removeVmPackageResolver(userId: Int, packageName: String) {
        val resolver = vmUserResolver.get(userId)
        if (resolver != null) {
            val key = getUserPkgKey(userId, packageName)
            iDataStorage.remove(key)
        }
    }

    fun loadAllVmPackageResolver(userId: Int): List<VmPackage> {
        val keys = iDataStorage.keys()
        val userPkKeys = keys.filter {
            it.startsWith(String.format(PACKAGE_RESOLVER_PREF_FORMAT, userId))
        }.toList()
        if (userPkKeys.isEmpty()) {
            return emptyList()
        }
        val result = ArrayList<VmPackage>(userPkKeys.size)
        for (userPkKey in userPkKeys) {
            val data = iDataStorage.load(userPkKey, VmPackage::class.java)
            if (data != null) {
                result.add(data)
            }
        }
        return result
    }

    fun checkPackageResolverExists(packageName: String, userId: Int): Boolean {
        if (!vmUserResolver.containsKey(userId)) {
            return false
        }
        val userPkgKey = getUserPkgKey(userId, packageName)
        return iDataStorage.containKey(userPkgKey)
    }

    fun loadVmPackageResolver(packageName: String, userId: Int): VmPackage? {
        val userPkgKey = getUserPkgKey(userId, packageName)
        return iDataStorage.load(userPkgKey, VmPackage::class.java)
    }

    fun queryActivities(resolverIntent: Intent, packageName: String, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo> {
        return queryComponents(ActivityInfo::class.java, resolverIntent, packageName, resolvedType, flags, userId)
    }

    fun queryServices(resolverIntent: Intent?, packageName: String, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo>{
        return queryComponents(ServiceInfo::class.java, resolverIntent, packageName, resolvedType, flags, userId)
    }

    fun queryProviders(resolverIntent: Intent?, packageName: String, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo>{
        return queryComponents(ProviderInfo::class.java, resolverIntent, packageName, resolvedType, flags, userId)
    }

    fun queryProviders(processName: String?, metaDataKey: String?, flags: Int, userId: Int): List<ProviderInfo>{
        if (!vmUserResolver.containsKey(userId)){
            return emptyList()
        }
        val vmPackageResolver = vmUserResolver.get(userId)
        val list = vmPackageResolver.queryProviders(processName, metaDataKey, flags, userId)
        list.sortBy { it.initOrder }
        return list
    }

    fun queryProvider(authority: String?, flags: Int, userId: Int):ProviderInfo?{
        if (!vmUserResolver.containsKey(userId)){
            return null
        }
        return vmUserResolver.get(userId).queryProvider(authority, flags, userId)
    }

    fun queryComponents(comType: Class<out ComponentInfo>, resolverIntent: Intent?, packageName: String, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo>{
        if (!vmUserResolver.containsKey(userId)){
            return emptyList()
        }
        val userPkgKey = getUserPkgKey(userId, packageName)
        if (!iDataStorage.containKey(userPkgKey)){
            return emptyList()
        }
        // 存在包名，则从解析包中查找
        val loadVmPackageResolverLock = loadVmPackageResolver(packageName, userId)
        val vmPackageResolver = vmUserResolver.get(userId)
        when(comType){
            ActivityInfo::class.java -> {
                if (loadVmPackageResolverLock?.activities != null) {
                    // 指定包进行解析，比较快
                    return vmPackageResolver.queryActivities(resolverIntent, resolvedType, flags, loadVmPackageResolverLock.activities, userId)
                }
                return vmPackageResolver.queryActivities(resolverIntent, resolvedType, flags, userId)
            }
            ServiceInfo::class.java ->{
                if (loadVmPackageResolverLock?.services != null){
                    // 指定包进行解析，比较快
                    return vmPackageResolver.queryServices(resolverIntent, resolvedType, flags, loadVmPackageResolverLock.services,userId)
                }
                return vmPackageResolver.queryServices(resolverIntent, resolvedType, flags, userId)
            }
            ProviderInfo::class.java ->{

            }
        }
        return emptyList()
    }

    private fun getUserPkgKey(userId: Int, packageName: String): String {
        return String.format(PACKAGE_RESOLVER_KEY_FORMAT, userId, packageName)
    }

    companion object {
        const val PACKAGE_RESOLVER_PREF_FORMAT = "resolved_pkg_%s_"
        const val PACKAGE_RESOLVER_KEY_FORMAT = "resolved_pkg_%s_%s"
    }
}