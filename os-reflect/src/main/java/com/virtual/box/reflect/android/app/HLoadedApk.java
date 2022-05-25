package com.virtual.box.reflect.android.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Instrumentation;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Build;

import com.virtual.box.reflect.MirrorReflection;

import java.io.File;
import java.lang.annotation.Target;

public class HLoadedApk {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.LoadedApk");
    public static MirrorReflection.FieldWrapper<ApplicationInfo> mApplicationInfo = REF.field("mApplicationInfo");
    public static MirrorReflection.MethodWrapper<Application> makeApplication = REF.method("makeApplication", boolean.class, Instrumentation.class);
    public static MirrorReflection.MethodWrapper<ClassLoader> getClassloader = REF.method("getClassloader");

    public static MirrorReflection.FieldWrapper<Boolean> mSecurityViolation = REF.field("mSecurityViolation");
    public static MirrorReflection.FieldWrapper<ClassLoader> mBaseClassLoader = REF.field("mBaseClassLoader");
    public static MirrorReflection.FieldWrapper<ClassLoader> mDefaultClassLoader = REF.field("mDefaultClassLoader");
    public static MirrorReflection.FieldWrapper<ClassLoader> mClassLoader = REF.field("mClassLoader");

    public static MirrorReflection.FieldWrapper<String> mResDir = REF.field("mResDir");

    @TargetApi(Build.VERSION_CODES.P)
    public static MirrorReflection.FieldWrapper<String> mLibDir = REF.field("mLibDir");
    public static MirrorReflection.FieldWrapper<Resources> mResources = REF.field("mResources");
    public static MirrorReflection.FieldWrapper<File> mCredentialProtectedDataDirFile = REF.field("mCredentialProtectedDataDirFile");
    public static MirrorReflection.FieldWrapper<String> mDataDir = REF.field("mDataDir");
    public static MirrorReflection.FieldWrapper<File> mDataDirFile = REF.field("mDataDirFile");
    public static MirrorReflection.FieldWrapper<File> mDeviceProtectedDataDirFile = REF.field("mDeviceProtectedDataDirFile");


}