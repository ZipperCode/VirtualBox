package com.virtual.box.reflect.android.os;

import android.os.Parcel;

import com.virtual.box.reflect.MirrorReflection;

public class HParcel {

    public static final MirrorReflection REF = MirrorReflection.on(Parcel.class);

    public static MirrorReflection.MethodWrapper<String> readString8 = REF.method("readString8");
    public static MirrorReflection.MethodWrapper<String[]> createString8Array = REF.method("createString8Array");


    public static MirrorReflection.MethodWrapper<Void> writeString8Array = REF.method("writeString8Array", String[].class);

    public static MirrorReflection.MethodWrapper<Void> restoreAllowSquashing = REF.method("restoreAllowSquashing", boolean.class);
}
