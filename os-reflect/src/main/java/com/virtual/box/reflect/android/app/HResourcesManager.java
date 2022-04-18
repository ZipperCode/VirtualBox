package com.virtual.box.reflect.android.app;

import android.util.ArraySet;

import com.virtual.box.reflect.MirrorReflection;

public class HResourcesManager {

    public static final MirrorReflection REF = MirrorReflection.on("android.app.ResourcesManager");

    public static final MirrorReflection.FieldWrapper<ArraySet<String>> mApplicationOwnedApks = REF.field("mApplicationOwnedApks");
}
