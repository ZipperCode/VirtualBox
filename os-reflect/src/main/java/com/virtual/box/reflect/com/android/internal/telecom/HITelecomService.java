package com.virtual.box.reflect.com.android.internal.telecom;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HITelecomService {
    public static class Stub {
        public static final MirrorReflection REF = MirrorReflection.on("com.android.internal.telecom.ITelecomService$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
