package com.virtual.box.reflect.com.android.internal.app;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;


public class HIAppOpsService {
    public static class Stub {
        public static final String NAME = "com.android.internal.app.IAppOpsService$Stub";
        private static final MirrorReflection REF = MirrorReflection.on(NAME);
        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}