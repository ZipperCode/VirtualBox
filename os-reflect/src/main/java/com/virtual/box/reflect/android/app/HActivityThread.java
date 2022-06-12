package com.virtual.box.reflect.android.app;


import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.util.ArrayMap;

import java.util.List;
import java.util.Map;

import com.virtual.box.reflect.MirrorReflection;


public class HActivityThread {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread");

    public static MirrorReflection.StaticMethodWrapper<Object> currentActivityThread = REF.staticMethod("currentActivityThread");
    public static MirrorReflection.StaticMethodWrapper<Object> currentProcessName = REF.staticMethod("currentProcessName");
    public static MirrorReflection.StaticMethodWrapper<Object> currentPackageName = REF.staticMethod("currentPackageName");
    public static MirrorReflection.FieldWrapper<Object> mBoundApplication = REF.field("mBoundApplication");
    public static MirrorReflection.FieldWrapper<Handler> mH = REF.field("mH");
    public static MirrorReflection.FieldWrapper<Application> mInitialApplication = REF.field("mInitialApplication");
    public static MirrorReflection.FieldWrapper<ArrayMap<Object, Object>> mProviderMap = REF.field("mProviderMap");
    public static MirrorReflection.FieldWrapper<Instrumentation> mInstrumentation = REF.field("mInstrumentation");
    public static MirrorReflection.FieldWrapper<IInterface> sPackageManager = REF.field("sPackageManager");
    public static MirrorReflection.FieldWrapper<IInterface> mAppThread = REF.field("mAppThread");
    public static MirrorReflection.MethodWrapper<IBinder> getApplicationThread = REF.method("getApplicationThread");
    public static MirrorReflection.MethodWrapper<Object> getSystemContext = REF.method("getSystemContext");

    /**
     * List<ProviderInfo>
     */
    public static MirrorReflection.MethodWrapper<Object> installSystemProviders = REF.method("installSystemProviders",List.class);
    public static MirrorReflection.MethodWrapper<Object> getPackageInfoNoCheck = REF.method("getPackageInfoNoCheck");
    public static MirrorReflection.FieldWrapper<Map<String,Object>> mPackages = REF.field("mPackages");
    public static MirrorReflection.FieldWrapper<Map<String,Object>> mResourcePackages = REF.field("mResourcePackages");


    public static class ActivityClientRecord {
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$ActivityClientRecord");
        public static MirrorReflection.FieldWrapper<Activity> activity = REF.field("activity");
        public static MirrorReflection.FieldWrapper<ActivityInfo> activityInfo = REF.field("activityInfo");
        public static MirrorReflection.FieldWrapper<Intent> intent = REF.field("intent");
        public static MirrorReflection.FieldWrapper<Object> packageInfo = REF.field("packageInfo");
        public static MirrorReflection.FieldWrapper<IBinder> token = REF.field("token");

    }

    public static class AppBindData {
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$AppBindData");
        public static MirrorReflection.FieldWrapper<ApplicationInfo> appInfo = REF.field("appInfo");
        public static MirrorReflection.FieldWrapper<Object> info = REF.field("info");
        public static MirrorReflection.FieldWrapper<String> processName = REF.field("processName");
        public static MirrorReflection.FieldWrapper<ComponentName> instrumentationName = REF.field("instrumentationName");
        public static MirrorReflection.FieldWrapper<List<ProviderInfo>> providers = REF.field("providers");
    }

    public static class H {
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$H");
        public static MirrorReflection.FieldWrapper<Integer> BIND_APPLICATION = REF.field("BIND_APPLICATION");
        public static MirrorReflection.FieldWrapper<Integer> LAUNCH_ACTIVITY = REF.field("LAUNCH_ACTIVITY");
        public static MirrorReflection.FieldWrapper<Integer> EXECUTE_TRANSACTION = REF.field("EXECUTE_TRANSACTION");
    }


    public static class CreateServiceData{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$CreateServiceData");
        /**
         * CompatibilityInfo
         */
        public static MirrorReflection.FieldWrapper<Object> compatInfo = REF.field("compatInfo");
        public static MirrorReflection.FieldWrapper<ServiceInfo> info = REF.field("info");
        public static MirrorReflection.FieldWrapper<Intent> intent = REF.field("intent");
    }

    public static class BindServiceData{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$BindServiceData");
        /**
         * CompatibilityInfo
         */
        public static MirrorReflection.FieldWrapper<Intent> intent = REF.field("intent");
    }

    public static class ServiceArgsData{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$ServiceArgsData");

        public static MirrorReflection.FieldWrapper<Intent> args = REF.field("args");
    }


    public static class ProviderClientRecord{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityThread$ProviderClientRecord");

        public static final MirrorReflection.FieldWrapper<String[]> mNames = REF.field("mNames");
        public static final MirrorReflection.FieldWrapper<IInterface> mProvider = REF.field("mProvider");
    }




}
