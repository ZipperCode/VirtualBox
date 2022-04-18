package com.virtual.box.reflect.android.media;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIMediaProjectionManager {

    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.media.projection.IMediaProjectionManager$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
    }
}
