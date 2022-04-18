package com.virtual.box.reflect.android.util;


import com.virtual.box.reflect.MirrorReflection;

public class HSingleton {
    public static final MirrorReflection REF = MirrorReflection.on("android.util.Singleton");
    public static MirrorReflection.MethodWrapper<Object> get = REF.method("get");
    public static MirrorReflection.FieldWrapper<Object> mInstance = REF.field("mInstance");
}
