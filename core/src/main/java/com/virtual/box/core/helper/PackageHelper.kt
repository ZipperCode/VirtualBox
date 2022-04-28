package com.virtual.box.core.helper

import android.content.Context
import android.content.pm.*
import android.os.Build
import android.os.Parcel
import androidx.annotation.WorkerThread
import com.virtual.box.base.ext.checkAndCreate
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import com.virtual.box.reflect.android.content.pm.HPackageInfo
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.collections.ArrayList

object PackageHelper {

    private const val TAG = "LibraryHelper"

    @JvmStatic
    @WorkerThread
    @Throws(Exception::class)
    fun copyLibrary(apkFile: File, targetLibRootDir: File) {
        val startTime = System.currentTimeMillis()
        if (!targetLibRootDir.exists()) {
            targetLibRootDir.mkdirs()
        }
        ZipFile(apkFile).use {
            findAndCopyLibrary(it, targetLibRootDir)
        }
        L.sd("$TAG >> 文件：${apkFile.absolutePath} 运行库拷贝成功，耗时：${System.currentTimeMillis() - startTime}")
    }

    @Synchronized
    fun saveInstallPackageInfo(vmPackageInfo: PackageInfo, file: File) {
        file.checkAndCreate()
        FileOutputStream(file).use { output ->
            val parcal = Parcel.obtain()
            try {
                parcal.setDataPosition(0)
                vmPackageInfo.writeToParcel(parcal, 0)
                val byte = parcal.marshall()
                output.write(byte)
            } finally {
                parcal.recycle()
            }
        }
    }

    @Synchronized
    fun loadInstallPackageInfo(packageName: String): PackageInfo {
        val packageInfoFile = VmFileSystem.getInstallAppPackageInfoFile(packageName)
        FileInputStream(packageInfoFile).use { input ->
            val parcal = Parcel.obtain()
            try {
                parcal.setDataPosition(0)
                val readBytes = input.readBytes()
                parcal.unmarshall(readBytes, 0, readBytes.size)
                parcal.setDataPosition(0)
                return PackageInfo.CREATOR.createFromParcel(parcal)
            } finally {
                parcal.recycle()
            }
        }
    }

    fun getInstallPackageInfo(hostPackageInfo: PackageInfo, aPackage: PackageParser.Package): PackageInfo {
        val parcel = Parcel.obtain()
        try {
            parcel.setDataPosition(0)
            hostPackageInfo.writeToParcel(parcel, 0)
            val vmPackageInfo = PackageInfo.CREATOR.createFromParcel(parcel)
            vmPackageInfo.apply {
                packageName = aPackage.packageName
                splitNames = emptyArray()
                versionCode = aPackage.mVersionCode
                versionName = aPackage.mVersionName
                HPackageInfo.compileSdkVersion.set(this, aPackage.mCompileSdkVersion)
                HPackageInfo.compileSdkVersionCodename.set(this, aPackage.mCompileSdkVersionCodename)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    baseRevisionCode = aPackage.baseRevisionCode
                    splitRevisionCodes = aPackage.splitRevisionCodes
                }
                sharedUserId = aPackage.mSharedUserId
                sharedUserLabel = aPackage.mSharedUserLabel
                applicationInfo = aPackage.applicationInfo
                applicationInfo.metaData = aPackage.mAppMetaData
                applicationInfo.processName = applicationInfo.packageName

                // 替换网络安全配置文件为宿主的，TODO 后续看下是否会找不到资源
                HApplicationInfo.networkSecurityConfigRes.set(
                    applicationInfo,
                    HApplicationInfo.networkSecurityConfigRes.get(hostPackageInfo.applicationInfo)
                )

                // TODO 是用宿主的还是后续自己动态生成的
                applicationInfo.uid = hostPackageInfo.applicationInfo.uid

                firstInstallTime = System.currentTimeMillis()
                lastUpdateTime = System.currentTimeMillis()
                gids = intArrayOf()

                val activityList = ArrayList<ActivityInfo>(aPackage.activities.size)
                for (activity in aPackage.activities) {
                    val activityInfo = activity.info
                    activityList.add(activityInfo)
                }
                activities = activityList.toTypedArray()

                val receiverList = ArrayList<ActivityInfo>(aPackage.receivers.size)
                for (receiver in aPackage.receivers) {
                    receiverList.add(receiver.info)
                }
                receivers = receiverList.toTypedArray()

                val serviceList = ArrayList<ServiceInfo>(aPackage.services.size)
                for (service in aPackage.services) {
                    serviceList.add(service.info)
                }
                services = serviceList.toTypedArray()

                val providerList = ArrayList<ProviderInfo>(aPackage.providers.size)
                for (provider in aPackage.providers) {
                    providerList.add(provider.info)
                }
                providers = providerList.toTypedArray()

                val instrumentationList = ArrayList<InstrumentationInfo>(aPackage.instrumentation.size)
                for (instrumentation in aPackage.instrumentation) {
                    instrumentationList.add(instrumentation.info)
                }
                instrumentation = instrumentationList.toTypedArray()

                val permissionsList = ArrayList<PermissionInfo>(aPackage.permissions.size)
                for (permission in aPackage.permissions) {
                    permissionsList.add(permission.info)
                }
                permissions = permissionsList.toTypedArray()

//                    val permissionsGroupList = ArrayList<PermissionGroupInfo>(aPackage.permissionGroups.size)
//                    for (permissionGroup in aPackage.permissionGroups) {
//                        permissionsGroupList.add(permissionGroup.info)
//                    }
//                    per = permissionsGroupList.toTypedArray()

                requestedPermissions = aPackage.requestedPermissions?.toTypedArray() ?: emptyArray()
                requestedPermissionsFlags = IntArray(requestedPermissions.size)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    attributions = emptyArray()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    signingInfo = SigningInfo()
                    signatures = aPackage.mSigningDetails.signatures
                } else {
                    signatures = aPackage.mSignatures
                }
                configPreferences = aPackage.configPreferences?.toTypedArray() ?: emptyArray()
                reqFeatures = aPackage.reqFeatures?.toTypedArray() ?: emptyArray()
                featureGroups = if (aPackage.featureGroups != null) {
                    val featureGroupInfoList = ArrayList<FeatureGroupInfo>(aPackage.featureGroups.size)
                    for (featureGroup in aPackage.featureGroups) {
                        featureGroupInfoList.add(featureGroup)
                    }
                    featureGroupInfoList.toTypedArray()
                } else {
                    emptyArray()
                }
            }
            return vmPackageInfo
        } finally {
            parcel.recycle()
        }
    }

    /**
     * 获取到的包信息也比较全面，需要修复下部分路径
     */
    fun getInstallPackageInfoByFile(context: Context, filePath: String): PackageInfo?{
        val file = File(filePath)
        if (!file.exists()){
            return null
        }
        try {
            return context.packageManager.getPackageArchiveInfo(filePath, getPackageArchiveInfoAllFlag())
        }catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    fun fixInstallApplicationInfo(vmApplicationInfo: ApplicationInfo) {
        val packageName = vmApplicationInfo.packageName
        val is64 = VmFileSystem.checkArm64(packageName)
        val abiName = if (is64) {
            // 64
            "arm64-v8a"
        } else {
            "armeabi"
        }
        vmApplicationInfo.apply {
            nativeLibraryDir = if (is64) {
                VmFileSystem.getInstallAppArm64LibDir(packageName).absolutePath
            } else {
                VmFileSystem.getInstallAppArmLibDir(packageName).absolutePath
            }
            HApplicationInfo.primaryCpuAbi.set(this, abiName)
            HApplicationInfo.nativeLibraryRootDir.set(this, VmFileSystem.getInstallAppLibDir(packageName).absolutePath)
            val installFile = VmFileSystem.getInstallBaseApkFile(packageName)
            publicSourceDir = installFile.absolutePath
            sourceDir = installFile.absolutePath
            val installDir = VmFileSystem.getAppInstall(packageName)
            HApplicationInfo.scanPublicSourceDir.set(this, installDir)
            HApplicationInfo.scanSourceDir.set(this, installDir)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vmApplicationInfo.storageUuid = UUID.randomUUID()
            }
        }
    }

    fun fixRunApplicationInfo(vmApplicationInfo: ApplicationInfo, userId: Int) {
        val packageName = vmApplicationInfo.packageName
        val dataFileDir = VmFileSystem.getDataDir(packageName, userId)
        vmApplicationInfo.dataDir = dataFileDir.absolutePath
        if (BuildCompat.isAtLeastN) {
            HApplicationInfo.credentialProtectedDataDir.set(vmApplicationInfo, dataFileDir.absolutePath)
            HApplicationInfo.deviceProtectedDataDir.set(vmApplicationInfo, VmFileSystem.getDeDataDir(packageName, userId).absolutePath)
        }
    }


    fun createNewPackageInfo(oldPackageInfo: PackageInfo): PackageInfo {
        val parcel = Parcel.obtain()
        try {
            oldPackageInfo.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            return PackageInfo.CREATOR.createFromParcel(parcel)
        } finally {
            parcel.recycle()
        }
    }

    @Throws(Exception::class)
    private fun findAndCopyLibrary(zipFile: ZipFile, targetLibRootDir: File) {
        L.sd("LibraryHelper >> 查找并拷贝so库")
        val prefix = "lib/"
        val entries = zipFile.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() as ZipEntry
            var entryName = entry.name
            if (!entryName.startsWith(prefix)) {
                continue
            }
            entryName = entryName.replace("lib/", "")
            if (entryName.contains("armeabi-v7a")) {
                entryName = entryName.replace("armeabi-v7a", "arm")
            }
            if (entryName.contains("arm64-v8a")) {
                entryName = entryName.replace("arm64-v8a", "arm64")
            }
            val soFile = File(targetLibRootDir, entryName)
            if (!soFile.exists()) {
                if (soFile.parentFile?.exists() == false) {
                    soFile.parentFile?.mkdirs()
                }
                soFile.createNewFile()
            } else {
                if (soFile.length() == entry.size) {
                    continue
                }
            }
            val buffer = ByteArray(1024 * 512)
            L.sd("LibraryHelper >> 开始拷贝 $entryName 到 ${soFile.absolutePath}")
            FileOutputStream(soFile).use { output ->
                zipFile.getInputStream(entry).use { input ->
                    var count = input.read(buffer)
                    while (count > 0) {
                        output.write(buffer, 0, count)
                        count = input.read(buffer)
                    }
                }
            }
        }

    }

    fun getPackageArchiveInfoAllFlag(): Int {
        return (PackageManager.GET_ACTIVITIES or PackageManager.GET_CONFIGURATIONS
                or PackageManager.GET_GIDS or PackageManager.GET_INSTRUMENTATION
                or PackageManager.GET_INTENT_FILTERS or PackageManager.GET_META_DATA
                or PackageManager.GET_PERMISSIONS or PackageManager.GET_PROVIDERS
                or PackageManager.GET_RECEIVERS or PackageManager.GET_SERVICES
                or PackageManager.GET_SHARED_LIBRARY_FILES or PackageManager.GET_SIGNATURES
                or PackageManager.GET_URI_PERMISSION_PATTERNS or PackageManager.GET_DISABLED_COMPONENTS
                or PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS or PackageManager.GET_UNINSTALLED_PACKAGES
                or PackageManager.GET_SIGNING_CERTIFICATES or PackageManager.MATCH_UNINSTALLED_PACKAGES
                or PackageManager.MATCH_DISABLED_COMPONENTS or PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS
                or PackageManager.MATCH_SYSTEM_ONLY or PackageManager.MATCH_APEX or PackageManager.GET_ATTRIBUTIONS)
    }

    fun getPackageInfoAllFlag(): Int {
        return (PackageManager.GET_ACTIVITIES or PackageManager.GET_CONFIGURATIONS
                or PackageManager.GET_GIDS or PackageManager.GET_INSTRUMENTATION
                or PackageManager.GET_INTENT_FILTERS or PackageManager.GET_META_DATA
                or PackageManager.GET_PERMISSIONS or PackageManager.GET_PROVIDERS
                or PackageManager.GET_RECEIVERS or PackageManager.GET_SERVICES
                or PackageManager.GET_SHARED_LIBRARY_FILES or PackageManager.GET_SIGNATURES
                or PackageManager.GET_URI_PERMISSION_PATTERNS or PackageManager.GET_DISABLED_COMPONENTS
                or PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS or PackageManager.GET_UNINSTALLED_PACKAGES)
    }
}