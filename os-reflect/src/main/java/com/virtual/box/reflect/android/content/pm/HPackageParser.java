package com.virtual.box.reflect.android.content.pm;

import android.annotation.TargetApi;
import android.content.pm.PackageParser;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;

import com.virtual.box.reflect.MirrorReflection;

public class HPackageParser {
    public static final MirrorReflection REF = MirrorReflection.on(android.content.pm.PackageParser.class);

    public static MirrorReflection.MethodWrapper<Void> collectCertificates =
            REF.method("collectCertificates", android.content.pm.PackageParser.Package.class, int.class);

    @TargetApi(Build.VERSION_CODES.P)
    public static MirrorReflection.MethodWrapper<Void> collectCertificates28 =
            REF.method("collectCertificates", PackageParser.Package.class, boolean.class);

    public static MirrorReflection.ConstructorWrapper<android.content.pm.PackageParser> constructor =
            REF.constructor(String.class);

    public static MirrorReflection.MethodWrapper<android.content.pm.PackageParser.Package> parsePackage1 =
            REF.method("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);

    public static MirrorReflection.MethodWrapper<PackageParser.Package> parsePackage2 =
            REF.method("parsePackage", File.class, int.class);
}
