package com.virtual.box.reflect.android.app.job;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;


public class HIJobScheduler {
    public static class Stub {
        public static final MirrorReflection REF = MirrorReflection.on("android.app.job.IJobScheduler$Stub");
        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
