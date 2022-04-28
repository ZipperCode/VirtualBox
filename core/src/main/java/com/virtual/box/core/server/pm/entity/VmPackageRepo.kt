package com.virtual.box.core.server.pm.entity

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Debug
import com.virtual.box.base.storage.IParcelDataHandle
import com.virtual.box.base.storage.MapParcelDataHandle
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.manager.VmFileSystem

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmPackageRepo {

    private val configStorageHandle: IParcelDataHandle<VmPackageSettings> =
        MapParcelDataHandle(VmFileSystem.mInstallPackageInfoConfig.name
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
    fun checkPackageInfo(packageName: String): Boolean{
        return vmPackageConfig.containsKey(packageName)
    }

    @Synchronized
    fun getPackageInfoList(flag: Int): List<PackageInfo>{
        val result = ArrayList<PackageInfo>(vmPackageConfig.size)
        for (vmPackageSetting in vmPackageConfig.values) {
            val vmPackageInfo = vmPackageSetting.vmPackageInfo
            if (vmPackageInfo != null){
                val newPackageInfo = PackageHelper.createNewPackageInfo(vmPackageInfo)
                if (flag.and(PackageManager.GET_ACTIVITIES) == 0){
                    newPackageInfo.activities = emptyArray()
                }
                result.add(newPackageInfo)
            }
        }
        return result
    }


    @Synchronized
    fun syncData(){
        configStorageHandle.save(MAP_PACKAGE_INFO_KEY, vmPackageConfig)
    }

    companion object {
        private const val MAP_PACKAGE_INFO_KEY = "MAP_PACKAGE_INFO_KEY"
    }
}