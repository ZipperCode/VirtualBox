package com.virtual.box.reflect.android.hardware.display;

import android.os.IBinder;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HIDisplayManager {

    public static final MirrorReflection REF = MirrorReflection.on("android.hardware.display.IDisplayManager$Stub");

    public static MirrorReflection.StaticMethodWrapper<IInterface> asInterface = REF.staticMethod("asInterface", IBinder.class);
}
