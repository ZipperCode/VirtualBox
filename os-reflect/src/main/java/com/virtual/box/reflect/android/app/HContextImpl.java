package com.virtual.box.reflect.android.app;


import android.app.LoadedApk;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;

import com.virtual.box.reflect.MirrorReflection;

public class HContextImpl {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.ContextImpl");

    public static MirrorReflection.FieldWrapper<String> mBasePackageName = REF.field("mBasePackageName");
    public static MirrorReflection.FieldWrapper<LoadedApk> mPackageInfo = REF.field("mPackageInfo");
    public static MirrorReflection.FieldWrapper<PackageManager> mPackageManager = REF.field("mPackageManager");
    public static MirrorReflection.FieldWrapper<String> mOpPackageName = REF.field("mOpPackageName");
    public static MirrorReflection.FieldWrapper<ClassLoader> mClassLoader = REF.field("mClassLoader");

    public static MirrorReflection.FieldWrapper<ContentResolver> mContentResolver = REF.field("mContentResolver");
    public static MirrorReflection.FieldWrapper<Object> mResourcesManager = REF.field("mResourcesManager");
    public static MirrorReflection.FieldWrapper<Context> mOuterContext = REF.field("mOuterContext");




}
