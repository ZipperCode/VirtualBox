package com.virtual.box.reflect.android.app;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HActivityManagerNative {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityManagerNative");

    public static MirrorReflection.FieldWrapper<Object> gDefault = REF.field("gDefault");
    public static MirrorReflection.StaticMethodWrapper<IInterface> getDefault = REF.staticMethod("getDefault");
}
