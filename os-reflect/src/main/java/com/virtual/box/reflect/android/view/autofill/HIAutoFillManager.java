package com.virtual.box.reflect.android.view.autofill;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIAutoFillManager {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.view.autofill.IAutoFillManager$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
