package com.virtual.box.reflect.android.hardware.location;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIContextHubService {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.hardware.location.HIContextHubService$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
