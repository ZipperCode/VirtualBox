package com.virtual.box.reflect.android.app.usage;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIStorageStatsManager {

    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.usage.IStorageStatsManager$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
