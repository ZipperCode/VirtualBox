package com.virtual.box.reflect.android.content.pm;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;

import com.virtual.box.reflect.MirrorReflection;

public class HPackageInfo {
    public static final MirrorReflection REF = MirrorReflection.on(PackageInfo.class);

    public static final MirrorReflection.FieldWrapper<Integer> versionCodeMajor = REF.field("versionCodeMajor");
    public static final MirrorReflection.FieldWrapper<Boolean> isStub = REF.field("isStub");
    public static final MirrorReflection.FieldWrapper<Boolean> coreApp = REF.field("coreApp");
    public static final MirrorReflection.FieldWrapper<Boolean> requiredForAllUsers = REF.field("requiredForAllUsers");
    public static final MirrorReflection.FieldWrapper<String> restrictedAccountType = REF.field("restrictedAccountType");
    public static final MirrorReflection.FieldWrapper<String> requiredAccountType = REF.field("requiredAccountType");
    public static final MirrorReflection.FieldWrapper<String> overlayTarget = REF.field("overlayTarget");
    public static final MirrorReflection.FieldWrapper<String> overlayCategory = REF.field("overlayCategory");
    public static final MirrorReflection.FieldWrapper<Integer> overlayPriority = REF.field("overlayPriority");
    @TargetApi(Build.VERSION_CODES.Q)
    public static final MirrorReflection.FieldWrapper<Boolean> mOverlayIsStatic = REF.field("mOverlayIsStatic");
    public static final MirrorReflection.FieldWrapper<Integer> compileSdkVersion = REF.field("compileSdkVersion");
    public static final MirrorReflection.FieldWrapper<String> compileSdkVersionCodename = REF.field("compileSdkVersionCodename");
}
