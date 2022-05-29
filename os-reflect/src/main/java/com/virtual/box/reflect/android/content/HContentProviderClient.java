package com.virtual.box.reflect.android.content;

import android.content.ContentProviderClient;
import android.content.pm.ProviderInfo;
import android.os.IInterface;

import com.virtual.box.reflect.MirrorReflection;

public class HContentProviderClient {
    public static final MirrorReflection REF = MirrorReflection.on(ContentProviderClient.class);

    public static MirrorReflection.FieldWrapper<String> mPackageName = REF.field("mPackageName");
    public static MirrorReflection.FieldWrapper<IInterface> mContentProvider = REF.field("mContentProvider");
}
