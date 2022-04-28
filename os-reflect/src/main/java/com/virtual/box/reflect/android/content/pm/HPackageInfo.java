package com.virtual.box.reflect.android.content.pm;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.os.Build;

import com.virtual.box.reflect.MirrorReflection;

public class HPackageInfo {
    public static final MirrorReflection REF = MirrorReflection.on(PackageInfo.class);

    public static  MirrorReflection.FieldWrapper<Integer> versionCodeMajor = REF.field("versionCodeMajor");
    public static  MirrorReflection.FieldWrapper<Boolean> isStub = REF.field("isStub");
    public static  MirrorReflection.FieldWrapper<Boolean> coreApp = REF.field("coreApp");
    public static  MirrorReflection.FieldWrapper<Boolean> requiredForAllUsers = REF.field("requiredForAllUsers");
    public static  MirrorReflection.FieldWrapper<String> restrictedAccountType = REF.field("restrictedAccountType");
    public static  MirrorReflection.FieldWrapper<String> requiredAccountType = REF.field("requiredAccountType");
    public static  MirrorReflection.FieldWrapper<String> overlayTarget = REF.field("overlayTarget");
    public static  MirrorReflection.FieldWrapper<String> overlayCategory = REF.field("overlayCategory");
    public static  MirrorReflection.FieldWrapper<Integer> overlayPriority = REF.field("overlayPriority");
    @TargetApi(Build.VERSION_CODES.Q)
    public static  MirrorReflection.FieldWrapper<Boolean> mOverlayIsStatic = REF.field("mOverlayIsStatic");
    public static  MirrorReflection.FieldWrapper<Integer> compileSdkVersion = REF.field("compileSdkVersion");
    public static  MirrorReflection.FieldWrapper<String> compileSdkVersionCodename = REF.field("compileSdkVersionCodename");
}
