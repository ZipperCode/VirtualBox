package com.virtual.box.reflect.android.hardware.display;

import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HDisplayManagerGlobal {

    public static final MirrorReflection REF = MirrorReflection.on("android.hardware.display.DisplayManagerGlobal");

    public static MirrorReflection.StaticMethodWrapper<Object> sInstance = REF.staticMethod("getInstance");

    public static MirrorReflection.FieldWrapper<IInterface> mDm = REF.field("mDm");
}
