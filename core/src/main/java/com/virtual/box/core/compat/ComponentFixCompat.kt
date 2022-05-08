package com.virtual.box.core.compat

import android.app.Activity
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.helper.ContextHelper
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.core.manager.VmPackageManager
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.HActivity
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HLoadedApk
import com.virtual.box.reflect.android.content.pm.HActivityInfo
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import java.util.*

object ComponentFixCompat {

    fun fixActivityWithOnCreate(activity: Activity){
        val component = HActivity.mComponent.get(activity)
        val vmPackageName = component.packageName
        // fix referrer
        HActivity.mReferrer.set(activity, vmPackageName)
        // fix application
        fixApplicationOnActivity(activity)
        // fix activityInfo
        fixActivityInfoOnActivity2(component, activity)
        // fix configuration >> loadedApk.overrideConfig
        fixResourceOnActivity(activity)
        // fix packageName
        ContextHelper.fixPackageName(activity, vmPackageName)
        ContextHelper.fixBaseContextLoadApk(activity)
    }
    /**
     * 修复Activity中的Application对象
     */
    fun fixApplicationOnActivity(activity: Activity){
        try {
            if (BuildCompat.isAtLeastS){
                val vmApplication = HActivityThread.mInitialApplication.get(VirtualBox.get().mainAThread)
                HActivity.mApplication.set(activity, vmApplication)
            }
        }catch (e: Exception){
            L.printStackTrace(e)
        }
    }

//    fun fixActivityInfoOnActivity(component: ComponentName, activity: Activity){
//        try {
//            val vmActivityInfo = BPackageManager.get().getActivityInfo(component,
//                PackageManager.GET_ACTIVITIES,
//                BUserHandle.myUserId())
//            HActivity.mActivityInfo.set(activity, vmActivityInfo)
//        }catch (e: java.lang.Exception){
//            L.printStackTrace(e)
//            fixActivityInfoOnActivity2(component, activity)
//        }
//    }

    fun fixActivityInfoOnActivity2(component: ComponentName, activity: Activity){
        val vmPackageName = component.packageName
        val originActivity = HActivity.mActivityInfo.get(activity)
        HActivityInfo.name.set(originActivity, activity.javaClass.name)
        HActivityInfo.packageName.set(originActivity, vmPackageName)
//        VmPackageManager.getActivityInfo(component, 0, )
//        val vmApplicationInfo = BPackageManager.get().getActi
    //        vityInfo(component, 0, BUserHandle.myUserId())
//        HActivityInfo.applicationInfo.set(originActivity, vmApplicationInfo)
    }

    fun fixResourceOnActivity(activity: Activity){
        val vmResources = HLoadedApk.mResources.get(VirtualBox.get().mVmLoadedApk)
//        try {
//            val field = MirrorReflection.on(Activity::class.java).field<Resources>("mResources")
//            field.set(activity, vmResources)
//        }catch (e: Exception){
//            L.printStackTrace(e)
//        }

        try {
            val field = MirrorReflection.on(AppCompatActivity::class.java).field<Resources>("mResources")
            field.set(activity, vmResources)
        }catch (e: Exception){
            L.printStackTrace(e)
        }
    }

    fun fixApplicationAbi(applicationInfo: ApplicationInfo){
        val supportAbis = Build.SUPPORTED_ABIS
        val support32Abis = Build.SUPPORTED_32_BIT_ABIS
        L.vd("supportAbis = %s, support32Abis = %s", Arrays.toString(supportAbis), Arrays.toString(support32Abis))
        if (HApplicationInfo.primaryCpuAbi.get().isNullOrEmpty()){
            HApplicationInfo.primaryCpuAbi.set(applicationInfo, SystemHelper.getPrimaryCpuAbiName())
        }
        if (HApplicationInfo.secondaryCpuAbi.get().isNullOrEmpty()){
            HApplicationInfo.secondaryCpuAbi.set(applicationInfo, if (supportAbis.isNullOrEmpty()) "armeabi-v7a" else support32Abis[0])
        }
    }

    fun fixApplicationInfo(ai: ApplicationInfo, userId: Int){
        ai.uid = Process.myUid()
        // 修复数据区域 /data/data/{pks}/
        ai.dataDir = VmFileSystem.getDataDir(ai.packageName, userId).absolutePath
        // 修复lib目录 /data/app/{pkg}/lib
        HApplicationInfo.nativeLibraryRootDir.set(ai,
            VmFileSystem.getInstallAppLibDir(ai.packageName).absolutePath
        )
        if (BuildCompat.isAtLeastN){
            ai.deviceProtectedDataDir = VmFileSystem.getDeDataDir(ai.packageName, userId).getAbsolutePath()
            HApplicationInfo.deviceEncryptedDataDir.set(ai, ai.deviceProtectedDataDir);
            HApplicationInfo.credentialEncryptedDataDir.set(ai, ai.dataDir)
            HApplicationInfo.deviceProtectedDataDir.set(ai, ai.deviceProtectedDataDir)
            HApplicationInfo.credentialProtectedDataDir.set(ai, ai.dataDir)
        }
    }
}