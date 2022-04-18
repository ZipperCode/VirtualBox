package com.virtual.box.reflect.android.content.res;

import com.virtual.box.reflect.MirrorReflection;

public class HCompatibilityInfo {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.res.CompatibilityInfo");

    public static final MirrorReflection.FieldWrapper<Object> DEFAULT_COMPATIBILITY_INFO = REF.field("DEFAULT_COMPATIBILITY_INFO");
}
