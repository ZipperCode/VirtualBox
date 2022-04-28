package com.virtual.box.core.server.pm.entity

import android.Manifest
import android.content.pm.*
import android.os.Build
import android.os.Parcel
import com.virtual.box.reflect.android.content.pm.HPackageInfo
import com.virtual.box.reflect.android.os.HParcel

@Deprecated("")
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
}