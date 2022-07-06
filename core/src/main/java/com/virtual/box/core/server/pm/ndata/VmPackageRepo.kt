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
import com.virtual.box.core.server.pm.VmComponentResolver
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo
import com.virtual.box.core.server.pm.resolve.VmPackage
import com.virtual.box.core.server.user.BUserHandle
import java.io.File

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

    @WorkerThread
    fun initData(){
        // TODO 获取所有用户
        vmPkResolverSource.initData(listOf(0))
    }

    fun onInstallPackage(userId: Int, aPackage: PackageParser.Package, vmPackageConfigInfo: VmPackageConfigInfo, vmPackageInfo: PackageInfo){
        val packageName = aPackage.packageName
        try {
            vmPkConfSource.saveInstallPackageConfig(userId, vmPackageConfigInfo)
            vmPkResolverSource.saveVmPackageResolver(userId, VmPackage(aPackage))
            vmPiSource.savePackageInfo(userId, vmPackageInfo)
            vmAppDataSource.saveAppDataConf(userId, )
        }catch (e: Exception){
            // rollback
            vmPkConfSource.removePackageConfig(userId, packageName)
            vmPkResolverSource.removeVmPackageResolver(userId, packageName)
            vmPiSource.removePackageInfo(userId, packageName)
            throw e
        }
    }

    @Synchronized
    fun addInstallPackageInfoWithLock(aPackage: PackageParser.Package, vmPackageConfigInfo: VmPackageConfigInfo, vmPackageInfo: PackageInfo){
        try {
            vmPkConfSource.addInstallPackageInfoWithLock(vmPackageConfigInfo)
            vmPkResolverSource.addVmPackageResolverLock(VmPackage(aPackage))
            vmPiSource.saveInstallVmPackageLock(vmPackageInfo)
        }catch (e: Exception){
            logger.e(e)
            vmPkConfSource.removeInstallPackageInfoWithLock(aPackage.packageName)
            vmPkResolverSource.removeVmPackageResolverLock(aPackage.packageName)
            vmPiSource.removeInstallVmPackageInfoLock(aPackage.packageName)
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
            if (!vmPkResolverSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // 存在包名，则从解析包中查找
            val loadVmPackageResolverLock = vmPkResolverSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.activities != null){
                // 指定包进行解析，比较快
                return vmPackageResolver.queryActivities(resolverIntent, resolvedType, flags, loadVmPackageResolverLock.activities,userId)
            }
            return vmPackageResolver.queryActivities(resolverIntent, resolvedType, flags, userId)
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
            if (!vmPkResolverSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // 存在包名，则从解析包中查找
            val loadVmPackageResolverLock = vmPkResolverSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.services != null){
                // 指定包进行解析，比较快
                return vmPackageResolver.queryServices(resolverIntent, resolvedType, flags, loadVmPackageResolverLock.services,userId)
            }
            return vmPackageResolver.queryServices(resolverIntent, resolvedType, flags, userId)
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
            if (!vmPkResolverSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // 存在包名，则从解析包中查找
            val loadVmPackageResolverLock = vmPkResolverSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.providers != null){
                // 指定包进行解析，比较快
                return vmPackageResolver.queryProviders(resolverIntent, resolvedType, flags, loadVmPackageResolverLock.providers,userId)
            }
            return vmPackageResolver.queryProviders(resolverIntent, resolvedType, flags, userId)
        }

        return emptyList()
    }

    fun queryContentProviders(processName: String?, uid: Int, flags: Int, metaDataKey: String?): List<ProviderInfo> {
        val userId: Int = if (processName != null) BUserHandle.getUserId(uid) else BUserHandle.getCallingUserId()
//        if (VmUserManagerService.exists(userId)){
//            return emptyList()
//        }

        val list = vmPackageResolver.queryProviders(processName, metaDataKey, flags, userId)
        list.sortBy { it.initOrder }
        return list
    }

    fun resolveContentProvider(authority: String?, flags: Int, userId: Int): ProviderInfo? {
        return vmPackageResolver.queryProvider(authority, flags, userId)
    }

    fun getInstrumentationInfo(className: ComponentName, flags: Int): InstrumentationInfo? {
        val packageName = className.packageName
        val loadInstallVmPackageInfoLock = vmPiSource.loadInstallVmPackageInfoLock(packageName)
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

    fun queryInstrumentation(targetPackage: String, flags: Int): List<Parcelable> {
        val result = mutableListOf<Parcelable>()
        val loadInstallVmPackageInfoLock = vmPiSource.loadInstallVmPackageInfoLock(targetPackage)
        if (loadInstallVmPackageInfoLock?.instrumentation != null){
            result.addAll(loadInstallVmPackageInfoLock.instrumentation)
        }
        return result
    }

    fun checkNeedInstalledOrUpdated(packageName: String, versionCode: Long): Boolean {
        return !checkPackageInstalled(packageName) || (checkPackageInstalled(packageName) && checkPackageVersion(packageName, versionCode))
    }

    /**
     * 检查包是否安装
     */
    fun checkPackageInstalled(packageName: String): Boolean {
        return vmPkConfSource.vmPackageConfig.packageSetting.containsKey(packageName)
    }

    /**
     * 应用版本检查
     */
    fun checkPackageVersion(packageName: String, versionCode: Long): Boolean {
        if (!checkPackageInstalled(packageName)) {
            return false
        }
        return vmPkConfSource.vmPackageConfig.packageSetting[packageName]!!.installPackageInfoVersionCode < versionCode
    }

    /**
     * 添加安装包配置信息
     * 调用前需要保证安装包已经安装到指定的位置中
     */
    @Synchronized
    fun addInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean {
        return vmPkConfSource.addInstallPackageInfoWithLock(vmPackageConfigInfo)
    }


    @Synchronized
    fun updateInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean {
        val packageName = vmPackageConfigInfo.packageName
        if (!checkPackageInstalled(packageName)) {
            return false
        }
        return vmPkConfSource.updateInstallPackageInfoWithLock(vmPackageConfigInfo)
    }

    /**
     * 删除安装包数据
     */
    @Synchronized
    fun removeInstallPackageInfoWithLock(packageName: String) {
        vmPkConfSource.removeInstallPackageInfoWithLock(packageName)
    }

    /**
     * 移除安装用户的数据
     */
    @Synchronized
    fun remoteInstallPackageUserDataWithLock(packageName: String, userId: Int) {
        vmPkConfSource.remoteInstallPackageUserDataWithLock(packageName, userId)
    }

    @Synchronized
    fun getPackageInfoList(flag: Int): List<PackageInfo> {
        val result = ArrayList<PackageInfo>(vmPkConfSource.packageSettings.size)
        for (vmInstallPackageEntry in vmPkConfSource.packageSettings) {
            val vmPackageConf = vmInstallPackageEntry.value
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

    fun getVmPackageInfo(packageName: String, flags: Int): PackageInfo? {
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
        val packageInfo = vmPiSource.loadInstallVmPackageInfoLock(packageName)
        return packageInfo
    }

    fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo? {
        val vmPackageInfo = getVmPackageInfo(packageName, flags) ?: return null
        return vmPackageInfo.applicationInfo
    }

    fun getActivityInfo(componentName: ComponentName, flags: Int): ActivityInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags) ?: return null
        val findActivityInfo = vmPackageInfo.activities.find { it.name == componentName.className } ?: return null
        return ActivityInfo(findActivityInfo)
    }

    fun getActivityInfo(componentName: ComponentName, flags: Int, userId: Int): ActivityInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags) ?: return null
        val findActivityInfo = vmPackageInfo.activities.find { it.name == componentName.className } ?: return null
        PackageHelper.fixRunApplicationInfo(findActivityInfo.applicationInfo, userId)
        return findActivityInfo
    }

    fun getReceiverInfo(componentName: ComponentName, flags: Int, userId: Int): ActivityInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags) ?: return null
        val findReceiverInfo = vmPackageInfo.receivers.find { it.name == componentName.packageName } ?: return null
        if (findReceiverInfo.applicationInfo == null){
            findReceiverInfo.applicationInfo = vmPackageInfo.applicationInfo
        }
        PackageHelper.fixRunApplicationInfo(findReceiverInfo.applicationInfo, userId)
        return findReceiverInfo
    }

    fun getServiceInfo(componentName: ComponentName, flags: Int, userId: Int): ServiceInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags) ?: return null
        val findServiceInfo = vmPackageInfo.services.find { it.name == componentName.packageName } ?: return null
        if (findServiceInfo.applicationInfo == null){
            findServiceInfo.applicationInfo = vmPackageInfo.applicationInfo
        }
        PackageHelper.fixRunApplicationInfo(findServiceInfo.applicationInfo, userId)
        return findServiceInfo
    }

    fun getProviderInfo(componentName: ComponentName, flags: Int, userId: Int): ProviderInfo? {
        val vmPackageInfo = getVmPackageInfo(componentName.packageName, flags) ?: return null
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

    companion object {

    }
}