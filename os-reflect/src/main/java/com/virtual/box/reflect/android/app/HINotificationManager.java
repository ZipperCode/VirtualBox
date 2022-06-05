package com.virtual.box.reflect.android.app;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HINotificationManager {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.app.INotificationManager$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
