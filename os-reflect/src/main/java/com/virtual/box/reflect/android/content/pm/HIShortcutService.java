package com.virtual.box.reflect.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIShortcutService {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.content.pm.IShortcutService$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
