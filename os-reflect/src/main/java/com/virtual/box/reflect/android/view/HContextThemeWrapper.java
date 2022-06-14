package com.virtual.box.reflect.android.view;

import android.content.res.Resources;

import com.virtual.box.reflect.MirrorReflection;

public class HContextThemeWrapper {

    public static final MirrorReflection REF = MirrorReflection.on("android.view.ContextThemeWrapper");

    public static MirrorReflection.FieldWrapper<Integer> mThemeResource = REF.field("mThemeResource");
    public static MirrorReflection.FieldWrapper<Resources> mResources = REF.field("mResources");
    public static MirrorReflection.FieldWrapper<Resources.Theme> mTheme = REF.field("mTheme");


}
