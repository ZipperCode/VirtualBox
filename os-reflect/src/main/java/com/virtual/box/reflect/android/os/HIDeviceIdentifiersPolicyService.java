package com.virtual.box.reflect.android.os;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIDeviceIdentifiersPolicyService {
    public static class Stub {
        public static final MirrorReflection REF = MirrorReflection.on("android.os.IDeviceIdentifiersPolicyService$Stub");
        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
