package com.virtual.box.core.server.pm.data

import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.IParcelDataHandle
import com.virtual.box.base.storage.MapParcelDataHandle
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.manager.VmPackageInstallManager
import com.virtual.box.core.server.pm.entity.*
import com.virtual.box.core.server.user.VmUserManagerService
import java.io.File

class VmPackageDataSource {
    private val logger = Logger.getLogger(L.VM_TAG,"VmPackageDataStore")

    private val iDataStorage: IDataStorage = ParcelDataHelper.getDataStorageLock(StorageConstant.VM_PK_CONFIG_INFO)

    val vmPackageConfig: VmPackageSettings = iDataStorage.load(MAP_PACKAGE_INFO_KEY, VmPackageSettings())

    val packageSettings: HashMap<String, VmPackageConfigInfo> get() = vmPackageConfig.packageSetting
    /**
     * 添加安装包配置信息
     * 调用前需要保证安装包已经安装到指定的位置中
     */
    @Synchronized
    fun addInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean{
        logger.method("添加安装包配置信息 %s", vmPackageConfigInfo)
        try {
            val packageName = vmPackageConfigInfo.packageName
            vmPackageConfig.packageSetting[packageName] = vmPackageConfigInfo
            val userSpaceConfigInfo = if (vmPackageConfig.packageUserSpaceSetting.containsKey(VmFileSystem.SYSTEM_USER_ID)){
                vmPackageConfig.packageUserSpaceSetting[VmFileSystem.SYSTEM_USER_ID]!!
            }else{
                VmPackageUserSpaceConfigInfo(VmFileSystem.SYSTEM_USER_ID).apply {
                    vmPackageConfig.packageUserSpaceSetting[VmFileSystem.SYSTEM_USER_ID] = this
                }
            }
            val vmPackageUserSpace = VmPackageUserSpace(VmFileSystem.SYSTEM_USER_ID, packageName)
            vmPackageUserSpace.apply {
                // 存储安装文件的配置文件 {vmRoot}/data/app/{pkg}/packageInfo.conf
                installVmPackageInfoFilePath = vmPackageConfigInfo.installPackageInfoFilePath
                // 存储安装目录 {vmRoot}/data/app/{pkg}/
                installVmPackageDirPath = File(vmPackageConfigInfo.installPackageInfoFilePath).parent
                // 这边存储的是应用的数据空间 {vmRoot}/data/data/{pkg}/
                userPackageSpaceRootDirPath = VmFileSystem.getDataDir(packageName, VmFileSystem.SYSTEM_USER_ID).absolutePath
                lastInstallUpdateTime = System.currentTimeMillis()
            }

            userSpaceConfigInfo.addPackageUserSpace(vmPackageUserSpace)
            return true
        }finally {
            syncData()
        }
    }

    @Synchronized
    fun updateInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean{
        val packageName = vmPackageConfigInfo.packageName
        try {
            val packageConf = vmPackageConfig.packageSetting[packageName] ?: return false
            // 更新信息
            packageConf.apply {
                installOption = vmPackageConfigInfo.installOption
                installPackageApkFilePath = vmPackageConfigInfo.installPackageApkFilePath
                installPackageInfoFilePath = vmPackageConfigInfo.installPackageInfoFilePath
                installPackageInfoVersionCode = vmPackageConfigInfo.installPackageInfoVersionCode
                installPackageInfoVersionCodeName = vmPackageConfigInfo.installPackageInfoVersionCodeName
            }

            val userSpaceConfigInfo = vmPackageConfig.packageUserSpaceSetting[VmFileSystem.SYSTEM_USER_ID] ?: return false
            val userSpace = userSpaceConfigInfo.packageUserSpace[packageName] ?: return false
            userSpace.apply {
                // 存储安装文件的配置文件 {vmRoot}/data/app/{pkg}/packageInfo.conf
                installVmPackageInfoFilePath = vmPackageConfigInfo.installPackageInfoFilePath
                // 存储安装目录 {vmRoot}/data/app/{pkg}/
                installVmPackageDirPath = File(vmPackageConfigInfo.installPackageInfoFilePath).parent
                // 这边存储的是应用的数据空间 {vmRoot}/data/data/{pkg}/
                userPackageSpaceRootDirPath = VmFileSystem.getDataDir(packageName, VmFileSystem.SYSTEM_USER_ID).absolutePath
                isOpened = false
                lastInstallUpdateTime = System.currentTimeMillis()
            }
            return true
        }finally {
            syncData()
        }
    }

    /**
     * 删除安装包数据
     */
    @Synchronized
    fun removeInstallPackageInfoWithLock(packageName: String){
        try {
            // 先遍历用户空间，删除用户数据
            val needRemoveUsers = mutableSetOf<Int>()
            vmPackageConfig.packageUserSpaceSetting.forEach { (userId, value) ->
                val userSpacePkgConfIterator = value.packageUserSpace.iterator()
                while (userSpacePkgConfIterator.hasNext()){
                    val next = userSpacePkgConfIterator.next()
                    if (next.value.packageName == packageName){
                        userSpacePkgConfIterator.remove()
                        // 删除指定应用包下的用户空间
                        VmPackageInstallManager.deleteUserSpaceData(packageName, userId)
                    }
                }
                // 用户空间下如果没有任何安装的包了，那就删除此用户(系统用户不处理)
                if (value.packageUserSpace.isEmpty() && userId != VmFileSystem.SYSTEM_USER_ID){
                    needRemoveUsers.add(userId)
                }
            }
            // 如果需要删除用户
            for (needRemoveUser in needRemoveUsers) {
                vmPackageConfig.packageUserSpaceSetting.remove(needRemoveUser)
                VmUserManagerService.deleteUserIfExists(needRemoveUser)
            }
            // 删除安装包数据
            vmPackageConfig.packageSetting.remove(packageName)
            // 删除安装目录
            VmPackageInstallManager.uninstallVmPackage(packageName)
        }finally {
            syncData()
        }
    }

    /**
     * 移除安装用户的数据
     */
    @Synchronized
    fun remoteInstallPackageUserDataWithLock(packageName: String, userId: Int){
        logger.i("删除指定包的用户数据 packageName = %s, userId = %s", packageName, userId)
        try {
            vmPackageConfig.packageUserSpaceSetting[userId]?.run {
                val userSpacePkgConfIterator = packageUserSpace.iterator()
                while (userSpacePkgConfIterator.hasNext()){
                    val next = userSpacePkgConfIterator.next()
                    if (next.value.packageName == packageName){
                        userSpacePkgConfIterator.remove()
                        // 删除指定应用包下的用户空间
                        VmPackageInstallManager.deleteUserSpaceData(packageName, userId)
                    }
                }
            }
            if (!vmPackageConfig.checkPackageUsed(packageName)){
                logger.e("安装包packageName = %s已经不存在使用的用户了，卸载安装包",packageName)
                // 删除安装包数据
                vmPackageConfig.packageSetting.remove(packageName)
                VmPackageInstallManager.uninstallVmPackage(packageName)
            }
        }finally {
            syncData()
        }
    }

    @Synchronized
    fun syncData(){
        iDataStorage.save(MAP_PACKAGE_INFO_KEY, vmPackageConfig)
    }

    companion object{
        const val MAP_PACKAGE_INFO_KEY = "MAP_PACKAGE_INFO_KEY"
    }
}