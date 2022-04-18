package com.virtual.box.reflect.android.os;

import android.os.Parcel;

import com.virtual.box.reflect.MirrorReflection;

public class HBaseBundle {
    public static final MirrorReflection REF = MirrorReflection.on("android.os.BaseBundle");
    public static MirrorReflection.FieldWrapper<Parcel> mParcelledData = REF.field("mParcelledData");
}