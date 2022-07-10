package com.virtual.box.reflect.android.content;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIClipboard {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.content.IClipboard$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
