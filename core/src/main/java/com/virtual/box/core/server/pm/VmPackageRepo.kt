package com.virtual.box.core.server.pm

import android.content.ComponentName
import android.content.Intent
import android.content.pm.*
import android.os.Debug
import android.os.Parcelable
import android.os.UserHandle
import androidx.annotation.WorkerThread
import com.virtual.box.base.ext.isNotNullOrEmpty
import com.virtual.box.base.storage.IDataStorage
import com.virtual.box.base.storage.ParcelDataHelper
import com.virtual.box.base.util.log.L
import com.virtual.box.base.util.log.Logger
import com.virtual.box.core.constant.StorageConstant
import com.virtual.box.core.helper.PackageHelper
import com.virtual.box.core.server.pm.data.VmPackageDataSource
import com.virtual.box.core.server.pm.data.VmPackageInfoDataSource
import com.virtual.box.core.server.pm.data.VmPackageResolverDataSource
import com.virtual.box.core.server.pm.entity.VmPackageConfigInfo
import com.virtual.box.core.server.pm.resolve.VmPackage
import com.virtual.box.core.server.user.BUserHandle
import com.virtual.box.core.server.user.VmUserManagerService
import java.io.File

/**
 *
 * @author zhangzhipeng
 * @date   2022/4/27
 **/
@Deprecated("")
class VmPackageRepo(

) {
    private val vmPackageDataSource: VmPackageDataSource = VmPackageDataSource()
    private val vmPackageResolverDataSource: VmPackageResolverDataSource = VmPackageResolverDataSource()
    private val vmPackageInfoDataSource: VmPackageInfoDataSource = VmPackageInfoDataSource()

    private val logger = Logger.getLogger(L.VM_TAG, "VmPackageRepo")

    private val vmPackageResolver: VmComponentResolver = VmComponentResolver()



    @WorkerThread
    fun initData(){
        val loadAllVmPackageResolverLock = vmPackageResolverDataSource.loadAllVmPackageResolverLock()
        for (vmPackage in loadAllVmPackageResolverLock.values) {
            vmPackageResolver.addAllComponents(vmPackage)
        }
    }

    @Synchronized
    fun addInstallPackageInfoWithLock(aPackage: PackageParser.Package, vmPackageConfigInfo: VmPackageConfigInfo, vmPackageInfo: PackageInfo){
        try {
            vmPackageDataSource.addInstallPackageInfoWithLock(vmPackageConfigInfo)
            vmPackageResolverDataSource.addVmPackageResolverLock(VmPackage(aPackage))
            vmPackageInfoDataSource.saveInstallVmPackageLock(vmPackageInfo)
        }catch (e: Exception){
            logger.e(e)
            vmPackageDataSource.removeInstallPackageInfoWithLock(aPackage.packageName)
            vmPackageResolverDataSource.removeVmPackageResolverLock(aPackage.packageName)
            vmPackageInfoDataSource.removeInstallVmPackageInfoLock(aPackage.packageName)
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
            //????????????????????????????????????????????????????????? ????????????????????????ActivityInfo
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
            if (!vmPackageResolverDataSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // ???????????????????????????????????????
            val loadVmPackageResolverLock = vmPackageResolverDataSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.activities != null){
                // ?????????????????????????????????
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
            if (!vmPackageResolverDataSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // ???????????????????????????????????????
            val loadVmPackageResolverLock = vmPackageResolverDataSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.services != null){
                // ?????????????????????????????????
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
            if (!vmPackageResolverDataSource.checkPackageResolverExists(packageName)){
                return emptyList()
            }
            // ???????????????????????????????????????
            val loadVmPackageResolverLock = vmPackageResolverDataSource.loadVmPackageResolverLock(packageName)
            if (loadVmPackageResolverLock?.providers != null){
                // ?????????????????????????????????
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
        val loadInstallVmPackageInfoLock = vmPackageInfoDataSource.loadInstallVmPackageInfoLock(packageName)
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
        val loadInstallVmPackageInfoLock = vmPackageInfoDataSource.loadInstallVmPackageInfoLock(targetPackage)
        if (loadInstallVmPackageInfoLock?.instrumentation != null){
            result.addAll(loadInstallVmPackageInfoLock.instrumentation)
        }
        return result
    }

    fun checkNeedInstalledOrUpdated(packageName: String, versionCode: Long): Boolean {
        return !checkPackageInstalled(packageName) || (checkPackageInstalled(packageName) && checkPackageVersion(packageName, versionCode))
    }

    /**
     * ?????????????????????
     */
    fun checkPackageInstalled(packageName: String): Boolean {
        return vmPackageDataSource.vmPackageConfig.packageSetting.containsKey(packageName)
    }

    /**
     * ??????????????????
     */
    fun checkPackageVersion(packageName: String, versionCode: Long): Boolean {
        if (!checkPackageInstalled(packageName)) {
            return false
        }
        return vmPackageDataSource.vmPackageConfig.packageSetting[packageName]!!.installPackageInfoVersionCode < versionCode
    }

    /**
     * ???????????????????????????
     * ???????????????????????????????????????????????????????????????
     */
    @Synchronized
    fun addInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean {
        return vmPackageDataSource.addInstallPackageInfoWithLock(vmPackageConfigInfo)
    }


    @Synchronized
    fun updateInstallPackageInfoWithLock(vmPackageConfigInfo: VmPackageConfigInfo): Boolean {
        val packageName = vmPackageConfigInfo.packageName
        if (!checkPackageInstalled(packageName)) {
            return false
        }
        return vmPackageDataSource.updateInstallPackageInfoWithLock(vmPackageConfigInfo)
    }

    /**
     * ?????????????????????
     */
    @Synchronized
    fun removeInstallPackageInfoWithLock(packageName: String) {
        vmPackageDataSource.removeInstallPackageInfoWithLock(packageName)
    }

    /**
     * ???????????????????????????
     */
    @Synchronized
    fun remoteInstallPackageUserDataWithLock(packageName: String, userId: Int) {
        vmPackageDataSource.remoteInstallPackageUserDataWithLock(packageName, userId)
    }

    @Synchronized
    fun getPackageInfoList(flag: Int): List<PackageInfo> {
        val result = ArrayList<PackageInfo>(vmPackageDataSource.packageSettings.size)
        for (vmInstallPackageEntry in vmPackageDataSource.packageSettings) {
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
//            // ?????????????????????????????????
//            removeInstallPackageInfoWithLock(vmPackageConf.packageName)
//            return null
//        }
//
//        // TODO ???????????????flags?????????????????????????????????
//        val packageInfo = PackageHelper.loadInstallPackageInfoNoLock(confFile)
//        if (flags.and(PackageManager.GET_ACTIVITIES) == 0){
//            packageInfo.activities = emptyArray()
//        }
        val packageInfo = vmPackageInfoDataSource.loadInstallVmPackageInfoLock(packageName)
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