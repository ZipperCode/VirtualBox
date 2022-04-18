package com.virtual.box.reflect.android.app;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;


public class HApplicationThreadNative {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.ApplicationThreadNative");

    public static MirrorReflection.MethodWrapper<IInterface> asInterface = REF.method("asInterface", IBinder.class);
}
