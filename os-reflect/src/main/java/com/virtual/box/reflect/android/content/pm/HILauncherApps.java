package com.virtual.box.reflect.android.content.pm;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HILauncherApps {

    public static final class Stub {
        public static final MirrorReflection REF = MirrorReflection.on("android.content.pm.ILauncherApps$Stub");
        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface");
    }
}
