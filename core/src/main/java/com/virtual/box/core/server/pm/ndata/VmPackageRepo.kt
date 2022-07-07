package com.virtual.box.core.server.pm.ndata

import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import android.os.Parcelable
import androidx.annotation.WorkerThread
import com.virtual.box.base.ext.isNotNullOrEmpty
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.server.pm.entity.VmAppDataConfigInfo
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo
import com.virtual.box.core.server.pm.resolve.VmPackage
import com.virtual.box.core.server.user.BUserHandle
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
class VmPackageRepo(

) {

    private val vmPkConfSource = VmInstallPackageConfigDataSource()
    private val vmPkResolverSource = VmUserPackageResolverDataSource()
    private val vmPiSource = VmInstallPackageInfoDataSource()
    private val vmAppDataSource = VmAppDataConfigDataSource()

    private val logger = Logger.getLogger(L.VM_TAG, "VmPackageRepo")

    private val dataLock = Any()

    @WorkerThread
    fun initData(){
        // TODO 获取所有用户
        val userIdList = listOf(0)
        vmPkResolverSource.initData(userIdList)
        vmAppDataSource.initData(userIdList)
    }

    fun onInstallPackage(aPackage: PackageParser.Package, vmPackageConfigInfo: VmPackageConfigInfo, vmPackageInfo: PackageInfo,userId: Int){
        val packageName = aPackage.packageName
        synchronized(dataLock){
            try {
                vmPkConfSource.saveInstallPackageConfig(userId, vmPackageConfigInfo)
                vmPkResolverSource.saveVmPackageResolver(userId, VmPackage(aPackage))
                vmPiSource.savePackageInfo(userId, vmPackageInfo)
            }catch (e: Exception){
                // rollback
                vmPkConfSource.removePackageConfig(userId, packageName)
                vmPkResolverSource.removeVmPackageResolver(userId, packageName)
                vmPiSource.removePackageInfo(userId, packageName)
                throw e
            }
        }
    }

    fun createAppData(userId: Int, vmPackageConfigInfo: VmPackageConfigInfo){
        synchronized(dataLock){
            val uuidStr = UUID.randomUUID().toString()
            val dataPathSuffix = uuidStr.substring(0, uuidStr.indexOf("-") - 1)
            val appDataId: String = uuidStr
            val packageName = vmPackageConfigInfo.packageName
            try {
                val installPackageRootPath = VmFileSystem
                    .getInstallAppRootDir(packageName, userId).absolutePath
                val installVmPackageInfoFilePath = vmPackageConfigInfo.installPackageInfoFilePath
                val appDataDir = VmFileSystem.getAppDataDir(packageName, userId, dataPathSuffix)
                VmFileSystem.mkdirAppDataDirAsync(appDataDir)

                val appDataConfigInfo = VmAppDataConfigInfo(
                    appDataId, userId, packageName,
                    installPackageRootPath,
                    appDataDir.absolutePath,
                    installVmPackageInfoFilePath
                )
                vmAppDataSource.saveAppDataConf(userId, appDataConfigInfo)
            }catch (e: Exception){
                VmFileSystem.removeAppDataDirAsync(packageName, userId, dataPathSuffix)
                vmAppDataSource.removeAppDataConf(userId, uuidStr)
                throw e
            }
        }
    }


    fun queryIntentActivities(intent: Intent, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo> {
        var comp = intent.component
        var resolverIntent: Intent = intent
        if (comp == null) {
            if (intent.selector != null) {
                resolverIntent = intent.selector!!
                comp = intent.component
            }
        }
        if (comp != null) {
            //如果指定饿模块和组名，则只有一个匹配项 通过模块信息得到ActivityInfo
            val fitAi = getActivityInfo(comp, flags, userId);
            if (fitAi != null){
                if (fitAi.processName.isNullOrEmpty()){
                    fitAi.processName = fitAi.packageName
                }
                return listOf(
                    ResolveInfo().apply {
                        activityInfo = fitAi
                    }
                )
            }
            return emptyList()
        }

        val packageName = resolverIntent.getPackage()

        if (packageName != null){
            return vmPkResolverSource.queryActivities(resolverIntent, packageName, resolvedType, flags, userId)
        }

        return emptyList()
    }

    fun queryIntentActivityOptions(
        componentName: ComponentName?,
        specifics: Array<out Intent>?,
        specificTypes: Array<out String>?,
        intent: Intent?,
        resolvedType: String?,
        flags: Int,
        userId: Int
    ): List<Parcelable> {
        val list = mutableListOf<Parcelable>()
        if (componentName != null){
            val activityInfo = getActivityInfo(componentName, flags, userId)
            if (activityInfo != null){
                val resolveInfo = ResolveInfo()
                resolveInfo.activityInfo = activityInfo
                list.add(resolveInfo)
            }
        }

        intent?.let {
            val queryIntentActivities = queryIntentActivities(it, resolvedType, flags, userId)
            list.addAll(queryIntentActivities)
        }

        specifics?.forEachIndexed { index, vIntent ->
            try {
                val queryIntentActivities = queryIntentActivities(vIntent, specificTypes?.get(index), flags, userId)
                list.addAll(queryIntentActivities)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        return list
    }

    fun queryIntentServices(intent: Intent, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo> {
        var resolverIntent: Intent? = intent
        var comp = resolverIntent?.component
        if (comp == null) {
            if (resolverIntent?.selector != null) {
                resolverIntent = resolverIntent.selector
                comp = resolverIntent!!.component
            }
        }
        if (comp != null) {
            val list: MutableList<ResolveInfo> = ArrayList(1)
            val si = getServiceInfo(comp, flags, userId)
            if (si != null) {
                val ri = ResolveInfo()
                ri.serviceInfo = si
                list.add(ri)
            }
            return list
        }

        val packageName = resolverIntent?.getPackage()

        if (packageName != null){
            vmPkResolverSource.queryServices(resolverIntent, packageName, resolvedType, flags, userId)
        }

        return emptyList()
    }

    fun queryIntentProviders(intent: Intent, resolvedType: String?, flags: Int, userId: Int): List<ResolveInfo> {
        var resolverIntent: Intent? = intent
        var comp = resolverIntent?.component
        if (comp == null) {
            if (resolverIntent?.selector != null) {
                resolverIntent = resolverIntent.selector
                comp = resolverIntent!!.component
            }
        }
        if (comp != null) {
            val list: MutableList<ResolveInfo> = ArrayList(1)
            val pi = getProviderInfo(comp, flags, userId)
            if (pi != null) {
                val ri = ResolveInfo()
                ri.providerInfo = pi
                list.add(ri)
            }
            return list
        }

        val packageName = resolverIntent?.getPackage()

        if (packageName != null){
           return vmPkResolverSource.queryProviders(resolverIntent, packageName, resolvedType, flags, userId)
        }

        return emptyList()
    }

    fun queryContentProviders(processName: String?, uid: Int, flags: Int, metaDataKey: String?): List<ProviderInfo> {
        val userId: Int = if (processName != null) BUserHandle.getUserId(uid) else BUserHandle.getCallingUserId()
//        if (VmUserManagerService.exists(userId)){
//            return emptyList()
//        }
        return vmPkResolverSource.queryProviders(processName, metaDataKey, flags, userId)
    }

    fun resolveContentProvider(authority: String?, flags: Int, userId: Int): ProviderInfo? {
        return vmPkResolverSource.queryProvider(authority, flags, userId)
    }

    fun getInstrumentationInfo(className: ComponentName, flags: Int, userId: Int): InstrumentationInfo? {
        val packageName = className.packageName
        val loadInstallVmPackageInfoLock = vmPiSource.loadInstallVmPackageInfo(packageName, userId)
        // TODO system PMS fix
        //    info.primaryCpuAbi = AndroidPackageUtils.getPrimaryCpuAbi(pkg, pkgSetting);
        //    info.secondaryCpuAbi = AndroidPackageUtils.getSecondaryCpuAbi(pkg, pkgSetting);
        //    info.nativeLibraryDir = pkg.getNativeLibraryDir();
        //    info.secondaryNativeLibraryDir = pkg.getSecondaryNativeLibraryDir();
        if (loadInstallVmPackageInfoLock != null){
            return loadInstallVmPackageInfoLock.instrumentation.find { it.name == className.className }
        }
        return null
    }

    fun queryInstrumentation(targetPackage: String, flags: Int, userId: Int): List<Parcelable> {
        val result = mutableListOf<Parcelable>()
        val loadInstallVmPackageInfoLock = vmPiSource.loadInstallVmPackageInfo(targetPackage, userId)
        if (loadInstallVmPackageInfoLock?.instrumentation != null){
            result.addAll(loadInstallVmPackageInfoLock.instrumentation)
        }
        return result
    }

    fun checkNeedInstalledOrUpdated(packageName: String, versionCode: Long, userId: Int): Boolean {
        return !checkPackageInstalled(packageName, userId)
                || (checkPackageInstalled(packageName, userId) && checkPackageVersion(packageName, versionCode, userId))
    }

    /**
     * 检查包是否安装
     */
    fun checkPackageInstalled(packageName: String, userId: Int): Boolean {
        return vmPkConfSource.checkPackageConfExists(userId, packageName)
    }

    /**
     * 应用版本检查
     */
    fun checkPackageVersion(packageName: String, versionCode: Long, userId: Int): Boolean {
        if (!checkPackageInstalled(packageName, userId)) {
            return false
        }

        return vmPkConfSource.getInstallPackageConfig(userId, packageName)!!.installPackageInfoVersionCode < versionCode
    }

    @Synchronized
    fun getPackageInfoList(flag: Int, userId: Int): List<PackageInfo> {
        val result = ArrayList<PackageInfo>()
        val userAllPackageUserConfList = vmPkConfSource.getUserAllPackageUserConfList(userId)
        for (vmPackageConf in userAllPackageUserConfList) {
            val confFile = File(vmPackageConf.installPackageInfoFilePath)
            if (confFile.exists()) {
                val packageInfo = PackageHelper.loadInstallPackageInfoNoLock(confFile)
                if (flag.and(PackageManager.GET_ACTIVITIES) == 0) {
                    packageInfo.activities = emptyArray()
                }

                if (packageInfo.packageName.isNotNullOrEmpty()) {
                    result.add(packageInfo)
                }
            }
        }
        return result
    }

    fun getVmPackageInfo(packageName: String, flags: Int, userId: Int): PackageInfo? {
//        val vmPackageConf = vmPackageDataSource.packageSettings[packageName] ?: return null
//        val file = File(vmPackageConf.installPackageApkFilePath)
//        val confFile = File(vmPackageConf.installPackageInfoFilePath)
//        if (!file.exists() || !confFile.exists()) {
//            // 文件不存在，删除此记录
//            removeInstallPackageInfoWithLock(vmPackageConf.packageName)
//            return null
//        }
//
//        // TODO 暂时不考虑flags标志，直接全家内容返回
//        val packageInfo = PackageHelper.loadInstallPackageInfoNoLock(confFile)
//        if (flags.and(PackageManager.GET_ACTIVITIES) == 0){
//            packageInfo.activities = emptyArray()
//        }
        val packageInfo = vmPiSource.loadInstallVmPackageInfo(packageName, userId)
        return packageInfo
    }

    fun getApplicationInfo(packageName: String, flags: Int, userId: Int): ApplicationInfo? {
        val vmPackageInfo = getVmPackageInfo(packageName, flags, userId) ?: return null
        return vmPackageInfo.applicationInfo
    }

    fun getActivityInfo(componentName: ComponentName, flags: Int, userId: Int): ActivityInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags, userId) ?: return null
        val findActivityInfo = vmPackageInfo.activities.find { it.name == componentName.className } ?: return null
        PackageHelper.fixRunApplicationInfo(findActivityInfo.applicationInfo, userId)
        return findActivityInfo
    }

    fun getReceiverInfo(componentName: ComponentName, flags: Int, userId: Int): ActivityInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags, userId) ?: return null
        val findReceiverInfo = vmPackageInfo.receivers.find { it.name == componentName.packageName } ?: return null
        if (findReceiverInfo.applicationInfo == null){
            findReceiverInfo.applicationInfo = vmPackageInfo.applicationInfo
        }
        PackageHelper.fixRunApplicationInfo(findReceiverInfo.applicationInfo, userId)
        return findReceiverInfo
    }

    fun getServiceInfo(componentName: ComponentName, flags: Int, userId: Int): ServiceInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags, userId) ?: return null
        val findServiceInfo = vmPackageInfo.services.find { it.name == componentName.packageName } ?: return null
        if (findServiceInfo.applicationInfo == null){
            findServiceInfo.applicationInfo = vmPackageInfo.applicationInfo
        }
        PackageHelper.fixRunApplicationInfo(findServiceInfo.applicationInfo, userId)
        return findServiceInfo
    }

    fun getProviderInfo(componentName: ComponentName, flags: Int, userId: Int): ProviderInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags, userId) ?: return null
        val findProviderInfo = vmPackageInfo.providers.find { it.name == componentName.packageName } ?: return null
        if (findProviderInfo.applicationInfo == null){
            findProviderInfo.applicationInfo = vmPackageInfo.applicationInfo
        }
        PackageHelper.fixRunApplicationInfo(findProviderInfo.applicationInfo, userId)
        return findProviderInfo
    }

    fun resolveActivities(): List<ResolveInfo> {
        return emptyList()
    }

}