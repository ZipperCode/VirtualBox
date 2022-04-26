package com.virtual.box.core.server.pm.entity

import android.Manifest
import android.content.pm.*
import android.os.Build
import android.os.Parcel
import com.virtual.box.reflect.android.content.pm.HPackageInfo
import com.virtual.box.reflect.android.os.HParcel

class VmPackageInfo() : PackageInfo(){

    constructor(source: Parcel):this(){
        packageName = HParcel.readString8.call(source)
        splitNames = HParcel.createString8Array.call(source)
        versionCode = source.readInt()
        HPackageInfo.versionCodeMajor.set(this, source.readInt())
        versionName = HParcel.readString8.call(source)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            baseRevisionCode = source.readInt()
            splitRevisionCodes = source.createIntArray()
        }
        sharedUserId = HParcel.readString8.call(source)
        sharedUserLabel = source.readInt()
        val hasApp = source.readInt()
        if (hasApp != 0) {
            applicationInfo = ApplicationInfo.CREATOR.createFromParcel(source)
        }
        firstInstallTime = source.readLong()
        lastUpdateTime = source.readLong()
        gids = source.createIntArray()
        activities = source.createTypedArray(ActivityInfo.CREATOR)
        receivers = source.createTypedArray(ActivityInfo.CREATOR)
        services = source.createTypedArray(ServiceInfo.CREATOR)
        providers = source.createTypedArray(ProviderInfo.CREATOR)
        instrumentation = source.createTypedArray(InstrumentationInfo.CREATOR)
        permissions = source.createTypedArray(PermissionInfo.CREATOR)
        requestedPermissions = HParcel.createString8Array.call(source)
        requestedPermissionsFlags = source.createIntArray()
        signatures = source.createTypedArray(Signature.CREATOR)
        configPreferences = source.createTypedArray(ConfigurationInfo.CREATOR)
        reqFeatures = source.createTypedArray(FeatureInfo.CREATOR)
        featureGroups = source.createTypedArray(FeatureGroupInfo.CREATOR)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            attributions = source.createTypedArray(Attribution.CREATOR)
        }
        installLocation = source.readInt()
        HPackageInfo.isStub.set(this, source.readInt() != 0)
        HPackageInfo.coreApp.set(this, source.readInt() != 0)
        HPackageInfo.requiredForAllUsers.set(this, source.readInt() != 0)
        HPackageInfo.restrictedAccountType.set(this, HParcel.readString8.call(source))
        HPackageInfo.requiredAccountType.set(this, HParcel.readString8.call(source))
        HPackageInfo.overlayTarget.set(this, HParcel.readString8.call(source))
        HPackageInfo.overlayCategory.set(this, HParcel.readString8.call(source))
        HPackageInfo.overlayPriority.set(this, source.readInt())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            HPackageInfo.mOverlayIsStatic.set(this, source.readBoolean())
        }
        HPackageInfo.compileSdkVersion.set(this, source.readInt())
        HPackageInfo.compileSdkVersionCodename.set(this, HParcel.readString8.call(source))
        val hasSigningInfo = source.readInt()
        if (hasSigningInfo != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                signingInfo = SigningInfo.CREATOR.createFromParcel(source)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isApex = source.readBoolean()
        }
    }

    companion object{

        fun convert(hostPackageInfo: PackageInfo, aPackage: PackageParser.Package): PackageInfo{
            val parcel = Parcel.obtain()
            try {
                parcel.setDataPosition(0)
                hostPackageInfo.writeToParcel(parcel, 0)
                val vmPackageInfo = VmPackageInfo(parcel)
                vmPackageInfo.apply {
                    packageName = aPackage.packageName
                    splitNames = emptyArray()
                    versionCode = aPackage.mVersionCode
                    versionName = aPackage.mVersionName
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        baseRevisionCode = aPackage.baseRevisionCode
                        splitRevisionCodes = aPackage.splitRevisionCodes
                    }
                    sharedUserId = aPackage.mSharedUserId
                    sharedUserLabel = aPackage.mSharedUserLabel

                    applicationInfo = aPackage.applicationInfo
                    applicationInfo.metaData = aPackage.mAppMetaData;
                    applicationInfo.sharedLibraryFiles
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

                    val serviceList =ArrayList<ServiceInfo>(aPackage.services.size)
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

                    requestedPermissions = aPackage.requestedPermissions.toTypedArray()
                    requestedPermissionsFlags = IntArray(requestedPermissions.size  )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        attributions = emptyArray()
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        signingInfo = SigningInfo()
                        signatures = aPackage.mSigningDetails.signatures
                    }else{
                        signatures = aPackage.mSignatures
                    }
                    configPreferences = aPackage.configPreferences.toTypedArray()
                    reqFeatures = aPackage.reqFeatures.toTypedArray()
                    val featureGroupInfoList = ArrayList<FeatureGroupInfo>(aPackage.featureGroups)
                    for (featureGroup in aPackage.featureGroups) {
                        featureGroupInfoList.add(featureGroup)
                    }
                    featureGroups = featureGroupInfoList.toTypedArray()
                }
                return vmPackageInfo
            }finally {
                parcel.recycle()
            }
        }
    }
}