package com.virtual.box.reflect.android.app;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HActivityClient {

    public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityClient");

    public static MirrorReflection.FieldWrapper<Object> INTERFACE_SINGLETON = REF.field("INTERFACE_SINGLETON");
    public static MirrorReflection.StaticMethodWrapper<Object> setActivityClientController = REF.staticMethod("setActivityClientController", Object.class);
    public static MirrorReflection.StaticMethodWrapper<Object> getActivityClientController = REF.staticMethod("getActivityClientController");

    public static class HActivityClientControllerSingleton{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.ActivityClient$ActivityClientControllerSingleton");

        public static MirrorReflection.FieldWrapper<IInterface> mKnownInstance = REF.field("mKnownInstance");

    }
}
