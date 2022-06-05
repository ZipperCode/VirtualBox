package com.virtual.box.reflect.android.view;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIGraphicsStats {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.view.IGraphicsStats$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
