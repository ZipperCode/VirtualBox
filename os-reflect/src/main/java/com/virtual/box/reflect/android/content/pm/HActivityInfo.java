package com.virtual.box.reflect.android.content.pm;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;

import com.virtual.box.reflect.MirrorReflection;

public class HActivityInfo {

    public static final MirrorReflection REF = MirrorReflection.on(ActivityInfo.class);
    public static final MirrorReflection REF_PARENT = MirrorReflection.on(ComponentInfo.class);
    public static final MirrorReflection REF_PARENT_PARENT = MirrorReflection.on(PackageItemInfo.class);

    public static final MirrorReflection.FieldWrapper<Integer> configChanges = REF.field("configChanges");
    public static final MirrorReflection.FieldWrapper<String> taskAffinity = REF.field("taskAffinity");
    public static final MirrorReflection.FieldWrapper<Integer> theme = REF.field("theme");
    public static final MirrorReflection.FieldWrapper<ApplicationInfo> applicationInfo = REF.field("applicationInfo");
    // com.zipper.virtual:p0
    public static final MirrorReflection.FieldWrapper<String> processName = REF.field("processName");
    // com.zipper.hook.proxy.ProxyActivity$P0
    public static final MirrorReflection.FieldWrapper<String> name = REF.field("name");
    public static final MirrorReflection.FieldWrapper<String> packageName = REF.field("packageName");

}
