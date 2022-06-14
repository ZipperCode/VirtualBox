package com.virtual.box.reflect.android.app;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;

import androidx.appcompat.view.ContextThemeWrapper;

import com.virtual.box.reflect.MirrorReflection;

public class HActivity {

    public static final MirrorReflection REF = MirrorReflection.on(Activity.class);

    public static final MirrorReflection.FieldWrapper<Application> mApplication = REF.field("mApplication");
    public static final MirrorReflection.FieldWrapper<ActivityInfo> mActivityInfo = REF.field("mActivityInfo");
    public static final MirrorReflection.FieldWrapper<String> mReferrer = REF.field("mReferrer");
    public static final MirrorReflection.FieldWrapper<ContextThemeWrapper> mBaseCompat = REF.field("mBase");
    public static final MirrorReflection.FieldWrapper<android.view.ContextThemeWrapper> mBase = REF.field("mBase");
    public static final MirrorReflection.FieldWrapper<ComponentName> mComponent = REF.field("mComponent");
    public static final MirrorReflection.FieldWrapper<CharSequence> mTitle = REF.field("mTitle");




}
