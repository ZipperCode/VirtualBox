package com.virtual.box.reflect.android.content.pm;

import android.os.Parcelable;

import com.virtual.box.reflect.MirrorReflection;

import java.util.List;

public class HParceledListSlice {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.pm.ParceledListSlice");
    public static MirrorReflection.MethodWrapper<Boolean> append = REF.method("append");
    public static MirrorReflection.ConstructorWrapper<Parcelable> constructor = REF.constructor();
    public static MirrorReflection.ConstructorWrapper<Parcelable> constructor1 = REF.constructor(List.class);

    public static MirrorReflection.MethodWrapper<Void> setLastSlice = REF.method("setLastSlice");


}
