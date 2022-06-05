package com.virtual.box.reflect.android.hardware.fingerprint;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIFingerprintService {
    public static class Stub{
        public static final MirrorReflection REF = MirrorReflection.on("android.hardware.fingerprint.IFingerprintService$Stub");

        public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);

    }
}
