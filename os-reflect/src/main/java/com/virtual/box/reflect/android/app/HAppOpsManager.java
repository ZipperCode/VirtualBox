package com.virtual.box.reflect.android.app;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HAppOpsManager {
    public static final MirrorReflection REF = MirrorReflection.on(android.app.AppOpsManager.class);
    public static MirrorReflection.FieldWrapper<IInterface> mService = REF.field("mService");
}
