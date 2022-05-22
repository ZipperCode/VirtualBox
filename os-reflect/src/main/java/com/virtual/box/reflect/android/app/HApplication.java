package com.virtual.box.reflect.android.app;

import android.app.Application;
import android.app.LoadedApk;
import android.content.Context;

import com.virtual.box.reflect.MirrorReflection;

public class HApplication {
    public static final MirrorReflection REF = MirrorReflection.on(Application.class);

    public static MirrorReflection.FieldWrapper<LoadedApk> mLoadedApk = REF.field("mLoadedApk");

    public static MirrorReflection.MethodWrapper<Void> attach = REF.method("attach", Context.class);

    public static MirrorReflection.MethodWrapper<Void> attachBaseContext = REF.method("attachBaseContext", Context.class);
}
