package com.virtual.box.core.server.pm.entity

import com.virtual.box.base.storage.IParcelDataHandle
import com.virtual.box.base.storage.MapParcelDataHandle
import com.virtual.box.core.manager.VmFileEnvironment

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmPackageRepo {

    private val configStorageHandle: IParcelDataHandle<VmPackageSettings> =
        MapParcelDataHandle(VmFileEnvironment.mInstallPackageInfoConfig.name
            .replace(".conf", ""), VmPackageSettings::class.java)

    private val vmPackageConfig: VmPackageSettings = configStorageHandle.load(MAP_PACKAGE_INFO_KEY) ?: VmPackageSettings()

    @Synchronized
    fun addPackageSetting(vmPackageSetting: VmPackageSetting){
        val key = vmPackageSetting.packageName
        vmPackageConfig[key] = vmPackageSetting
        syncData()
    }

    @Synchronized
    fun removePackageSetting(key: String){
        if (vmPackageConfig.containsKey(key)){
            vmPackageConfig.remove(key)
            syncData()
        }
    }

    @Synchronized
    fun syncData(){
        configStorageHandle.save(MAP_PACKAGE_INFO_KEY, vmPackageConfig)
    }

    companion object {
        private const val MAP_PACKAGE_INFO_KEY = "MAP_PACKAGE_INFO_KEY"
    }
}