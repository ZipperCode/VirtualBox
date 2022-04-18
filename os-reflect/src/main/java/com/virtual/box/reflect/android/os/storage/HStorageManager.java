package com.virtual.box.reflect.android.os.storage;

import android.os.storage.StorageVolume;

import com.virtual.box.reflect.MirrorReflection;

public class HStorageManager {
    public static final MirrorReflection REF = MirrorReflection.on("android.os.storage.StorageManager");

    public static MirrorReflection.StaticMethodWrapper<StorageVolume[]> getVolumeList = REF.staticMethod("getVolumeList", int.class, int.class);
}
