package com.virtual.box.reflect.android.os;

import com.virtual.box.reflect.MirrorReflection;

public class HHandler {
    public static final MirrorReflection REF = MirrorReflection.on(android.os.Handler.class);

    public static MirrorReflection.FieldWrapper<android.os.Handler.Callback> mCallback = REF.field("mCallback");
}
