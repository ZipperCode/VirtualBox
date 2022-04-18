package com.virtual.box.reflect.android.os;

import android.os.IBinder;
import android.os.Parcel;

import com.virtual.box.reflect.MirrorReflection;

public class HBundle {
    public static final MirrorReflection REF = MirrorReflection.on(android.os.Bundle.class);

    public static MirrorReflection.MethodWrapper<Void> putIBinder = REF.method("putIBinder", String.class, IBinder.class);

    public static MirrorReflection.MethodWrapper<IBinder> getIBinder = REF.method("getIBinder", String.class);

    public static MirrorReflection.FieldWrapper<Parcel> mParcelledData = REF.field("mParcelledData");
}
