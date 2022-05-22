package com.virtual.box.reflect.android.content;

import android.content.AttributionSource;
import android.content.pm.ProviderInfo;
import android.os.Build;
import android.os.IInterface;

import androidx.annotation.RequiresApi;

import com.virtual.box.reflect.MirrorReflection;

public class HAttributionSource {
    public static final MirrorReflection REF = MirrorReflection.on("android.content.AttributionSource");

    public static MirrorReflection.FieldWrapper<Object> mAttributionSourceState = REF.field("mAttributionSourceState");
    public static MirrorReflection.MethodWrapper<Object> getNext = REF.method("getNext");
}
