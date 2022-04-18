package com.virtual.box.reflect.android.app;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;


public class HActivityManager {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityManager");

    public static MirrorReflection.MethodWrapper<IInterface> getService = REF.method("getService");

    public static MirrorReflection.FieldWrapper<Object> IActivityManagerSingleton = REF.field("IActivityManagerSingleton");
}
