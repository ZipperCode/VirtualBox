package com.virtual.box.reflect.android.content.pm;

import com.virtual.box.reflect.MirrorReflection;

public class HSigningInfo {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.pm.SigningInfo");

    public static MirrorReflection.FieldWrapper<android.content.pm.PackageParser.SigningDetails> mSigningDetails = REF.field("mSigningDetails");
}
