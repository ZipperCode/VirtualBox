package com.virtual.box.reflect.android.os.storage;

import java.io.File;

import com.virtual.box.reflect.MirrorReflection;

public class HStorageVolume {
    public static final MirrorReflection REF = MirrorReflection.on("android.os.storage.StorageVolume");

    public static MirrorReflection.FieldWrapper<File> mPath = REF.field("mPath");
    public static MirrorReflection.FieldWrapper<File> mInternalPath = REF.field("mInternalPath");
}
