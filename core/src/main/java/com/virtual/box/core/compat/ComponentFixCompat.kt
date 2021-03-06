package com.virtual.box.core.compat

import android.app.Activity
import android.content.ComponentName
import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.os.Build
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.virtual.box.base.helper.SystemHelper
import com.virtual.box.base.util.compat.BuildCompat
import com.virtual.box.base.util.log.L
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.helper.ContextHelper
import com.virtual.box.core.manager.AppActivityThread
import com.virtual.box.core.manager.VmFileSystem
import com.virtual.box.reflect.MirrorReflection
import com.virtual.box.reflect.android.app.HActivity
import com.virtual.box.reflect.android.app.HActivityThread
import com.virtual.box.reflect.android.app.HLoadedApk
import com.virtual.box.reflect.android.content.pm.HActivityInfo
import com.virtual.box.reflect.android.content.pm.HApplicationInfo
import com.virtual.box.reflect.android.view.HContextThemeWrapper
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
        if (activity !is AppCompatActivity){
            val vmResources = HLoadedApk.mResources.get(AppActivityThread.getAppLoadedApk())
            val newTheme = vmResources.newTheme()
            HContextThemeWrapper.mResources.set(activity, vmResources)
            HContextThemeWrapper.mTheme.set(activity, newTheme)
            val curActInfo = HActivity.mActivityInfo.get(activity)
            if (curActInfo != null){
                HContextThemeWrapper.mThemeResource.set(activity, curActInfo.theme)
            }
            return
        }

        val vmResources = HLoadedApk.mResources.get(AppActivityThread.getAppLoadedApk())
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
        if (HApplicationInfo.primaryCpuAbi.get(applicationInfo).isNullOrEmpty()){
            HApplicationInfo.primaryCpuAbi.set(applicationInfo, SystemHelper.getPrimaryCpuAbiName())
        }
        if (HApplicationInfo.secondaryCpuAbi.get(applicationInfo).isNullOrEmpty()){
            HApplicationInfo.secondaryCpuAbi.set(applicationInfo, if (supportAbis.isNullOrEmpty()) "armeabi-v7a" else support32Abis[0])
        }
    }

    fun fixApplicationInfo(ai: ApplicationInfo, userId: Int){
        try {
            ai.uid = Process.myUid()
            // 修复数据区域 /data/data/{pks}/
            val dataDir = VmFileSystem.getDataDir(ai.packageName, userId)
            if (ai.dataDir.isNullOrEmpty()){
                ai.dataDir = dataDir.absolutePath
            }

            // 修复lib目录 /data/app/{pkg}/lib
            val installAppLibDir = VmFileSystem.getInstallAppLibDir(ai.packageName)
            HApplicationInfo.nativeLibraryRootDir.set(ai,
                installAppLibDir.absolutePath
            )
            val deDataDir = VmFileSystem.getDeDataDir(ai.packageName, userId)
            if (BuildCompat.isAtLeastN){
                ai.deviceProtectedDataDir = deDataDir.absolutePath
                HApplicationInfo.credentialProtectedDataDir.set(ai, ai.dataDir)
            }else{
                HApplicationInfo.deviceEncryptedDataDir.set(ai, deDataDir.absolutePath)
                HApplicationInfo.credentialEncryptedDataDir.set(ai, ai.dataDir)
            }
            //deDataDir.checkAndMkdirs()
            // VmFileSystem.mkdirAppData(ai.packageName, userId)
        }catch (e: Exception){
            L.printStackTrace(e)
        }
    }

    fun fixActivityConfig(activity: Activity){

    }

    fun fixActivityOrientation(activity: Activity){
        try {
            val activityInfo = HActivity.mActivityInfo.get(activity)
            val orientation = activityInfo.screenOrientation
            activity.requestedOrientation = orientation
            // TODO 8.1
        }catch (e: java.lang.Exception){
            L.printStackTrace(e)
        }
    }
}