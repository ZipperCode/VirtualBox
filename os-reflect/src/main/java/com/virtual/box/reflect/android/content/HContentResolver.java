package com.virtual.box.reflect.android.content;

import com.virtual.box.reflect.MirrorReflection;

public class HContentResolver {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.ContentResolver");

    public static final MirrorReflection.FieldWrapper<String> mPackageName = REF.field("mPackageName");
    public static final MirrorReflection.FieldWrapper<Integer> mTargetSdkVersion = REF.field("mTargetSdkVersion");
}
