package com.virtual.box.reflect.android.os;

import android.os.IBinder;
import android.os.IInterface;

import java.util.Map;

import com.virtual.box.reflect.MirrorReflection;

public class HServiceManager {
    public static final MirrorReflection REF = MirrorReflection.on("android.os.ServiceManager");
    public static MirrorReflection.StaticMethodWrapper<Void> addService = REF.staticMethod("addService", String.class, IBinder.class);
    public static MirrorReflection.StaticMethodWrapper<IInterface> getIServiceManager = REF.staticMethod("getIServiceManager");
    public static MirrorReflection.StaticMethodWrapper<IBinder> getService = REF.staticMethod("getService");
    public static MirrorReflection.FieldWrapper<Map<String, IBinder>> sCache = REF.field("sCache");
}
