package com.virtual.box.reflect.android.content.pm;

import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.virtual.box.reflect.MirrorReflection;

public class HApplicationInfo {
    public static final MirrorReflection REF = MirrorReflection.on(ApplicationInfo.class);

    public static MirrorReflection.FieldWrapper<String> primaryCpuAbi = REF.field("primaryCpuAbi");
    public static MirrorReflection.FieldWrapper<String> scanPublicSourceDir = REF.field("scanPublicSourceDir");
    public static MirrorReflection.FieldWrapper<String> scanSourceDir = REF.field("scanSourceDir");
    public static MirrorReflection.FieldWrapper<String> secondaryCpuAbi = REF.field("secondaryCpuAbi");
    public static MirrorReflection.FieldWrapper<String[]> splitPublicSourceDirs = REF.field("splitPublicSourceDirs");
    public static MirrorReflection.FieldWrapper<String> nativeLibraryRootDir = REF.field("nativeLibraryRootDir");

    @TargetApi(Build.VERSION_CODES.N)
    public static MirrorReflection.FieldWrapper<String> deviceProtectedDataDir = REF.field("deviceProtectedDataDir");
    @TargetApi(Build.VERSION_CODES.N)
    public static MirrorReflection.FieldWrapper<String> deviceEncryptedDataDir = REF.field("deviceEncryptedDataDir");
    @TargetApi(Build.VERSION_CODES.N)
    public static MirrorReflection.FieldWrapper<String> credentialProtectedDataDir = REF.field("credentialProtectedDataDir");
    @TargetApi(Build.VERSION_CODES.N)
    public static MirrorReflection.FieldWrapper<String> credentialEncryptedDataDir = REF.field("credentialEncryptedDataDir");

}
