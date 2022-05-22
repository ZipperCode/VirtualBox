package com.virtual.box.reflect.android.provider;

import android.content.Context;
import android.os.Build;
import android.provider.FontsContract;

import androidx.annotation.RequiresApi;

import com.virtual.box.reflect.MirrorReflection;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HFontsContract {

    public static final MirrorReflection REF = MirrorReflection.on(FontsContract.class);

    public static MirrorReflection.FieldWrapper<Context> sContext = REF.field("sContext");
}
