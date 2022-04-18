package com.virtual.box.reflect.android.app.servertransaction;

import java.util.List;

import com.virtual.box.reflect.MirrorReflection;

public class HClientTransaction {
    public static final MirrorReflection REF = MirrorReflection.on("android.app.servertransaction.ClientTransaction");

    public static MirrorReflection.FieldWrapper<List<Object>> mActivityCallbacks = REF.field("mActivityCallbacks");
}
