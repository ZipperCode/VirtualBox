package com.virtual.box.reflect.android;

import com.virtual.box.reflect.MirrorReflection;

public class HAttributionSourceState {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.AttributionSourceState");

    public static MirrorReflection.FieldWrapper<String> packageName = REF.field("packageName");
    public static MirrorReflection.FieldWrapper<Object[]> next = REF.field("next");
}
