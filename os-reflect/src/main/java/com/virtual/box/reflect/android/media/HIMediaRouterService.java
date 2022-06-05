package com.virtual.box.reflect.android.media;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIMediaRouterService {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.media.IMediaRouterService$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
